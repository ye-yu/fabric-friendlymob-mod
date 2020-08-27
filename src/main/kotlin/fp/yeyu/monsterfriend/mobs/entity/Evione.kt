package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.item.ItemRegistry
import fp.yeyu.monsterfriend.mobs.entity.ai.LookAtCustomerGoal
import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.screens.evione.EvioneServerScreenHandler
import fp.yeyu.monsterfriend.utils.ConfigFile
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ai.goal.EscapeDangerGoal
import net.minecraft.entity.ai.goal.LookAroundGoal
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import java.util.stream.IntStream

class Evione(
    entityType: EntityType<out PathAwareEntity>?,
    world: World?
) : PathAwareEntity(entityType, world), GuiProvider {

    private var spellCastingPoseTick: Int = -1
    override var currentUser: PlayerEntity? = null
    private val inventory = EvioneInventory(this)
    override val guiFactory = EvioneScreenFactory(this)
    private var synthesisProgress = 0

    companion object {
        val POSE_STATE: TrackedData<State> =
            DataTracker.registerData(Evione::class.java, State.TRACKED_STATE)

        const val MAX_PROGRESS = 100
        const val POSE_STATE_NAME = "pose_state"
        const val SYNTHESIS_PROGRESS_NAME = "synthesis_progress"

        fun createEvioneAttributes(): DefaultAttributeContainer.Builder {
            return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
        }

        fun isEssence(item: Item): Boolean {
            return item == ItemRegistry.vexEssence
        }

        private var MAX_SPELL_TICK: Int = ConfigFile.getInt(ConfigFile.Defaults.EVIONE_MAX_SPELL_TICK)
        private var SYNTHESIS_CHANCE = ConfigFile.getFloat(ConfigFile.Defaults.EVIONE_SYNTHESIS_CHANCE)
        private var SYNTHESIS_CAN_SPEED_UP_CHANCE =
            ConfigFile.getFloat(ConfigFile.Defaults.EVIONE_SYNTHESIS_CAN_SPEED_UP_CHANCE)
        private var SYNTHESIS_SPEED_UP_CHANCE =
            ConfigFile.getFloat(ConfigFile.Defaults.EVIONE_SYNTHESIS_SPEED_UP_CHANCE)

        private var SYNTHESIS_SPEED_UP_COUNT =
            ConfigFile.getInt(ConfigFile.Defaults.EVIONE_SYNTHESIS_SPEED_UP_COUNT).toLong()

        private var DROP_VEX_ESSENCE_CHANCE = ConfigFile.getFloat(ConfigFile.Defaults.EVIONE_DROP_VEX_ESSENCE_CHANCE)

        private const val SPELLCASTING_STATUS_NUMBER: Byte = 80

    }

    private fun resetSynthesis() {
        setProgress(0)
        val drop = getInventory().removeStack(2)
        if (drop.isEmpty) return
        dropStack(drop)
    }

    private fun setProgress(p: Int) {
        synthesisProgress = p.coerceAtLeast(0).coerceAtMost(100)
    }

    private fun getSynthesisSound(): SoundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_AMBIENT
    override fun getFallSound(distance: Int): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT
    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_DEATH

    private fun setState(state: State) {
        this.dataTracker.set(POSE_STATE, state)
    }

    fun getState(): State {
        return this.dataTracker.get(POSE_STATE)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(POSE_STATE, State.CROSSED)
    }

    override fun writeCustomDataToTag(tag: CompoundTag) {
        super.writeCustomDataToTag(tag)
        tag.putByte(POSE_STATE_NAME, getState().ordinal.toByte())
        tag.putByte(SYNTHESIS_PROGRESS_NAME, getSynthesisProgress().toByte())
        Inventories.toTag(tag, (getInventory() as EvioneInventory).toList())
    }

    override fun readCustomDataFromTag(tag: CompoundTag) {
        super.readCustomDataFromTag(tag)
        if (tag.contains(POSE_STATE_NAME)) setState(State.values()[tag.getByte(POSE_STATE_NAME).toInt()])
        val list = DefaultedList.ofSize(3, ItemStack.EMPTY)
        Inventories.fromTag(tag, list)
        (getInventory() as EvioneInventory).load(list)

        // load last because setting inventory item resets progress
        if (tag.contains(SYNTHESIS_PROGRESS_NAME)) setProgress(tag.getByte(SYNTHESIS_PROGRESS_NAME).toInt())
    }

    fun getSynthesisProgress(): Int {
        return synthesisProgress
    }

    private fun getSynthesisItem(): ItemStack {
        return getInventory().getStack(0)
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(0, EscapeDangerGoal(this, 0.8))
        goalSelector.add(0, LookAtCustomerGoal(this))
        goalSelector.add(1, LookAroundGoal(this))
        goalSelector.add(2, EvioneWanderAroundGoal(this, 0.6, 120, true))
        goalSelector.add(3, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
    }

    override fun mobTick() {
        super.mobTick()
        validatePose()
        synthesisItem()
        validateSynthesis()
        dropEssence()
    }

    private fun dropEssence() {
        if (isNight()) {
            // always roll at night
            if (random.nextDouble() < DROP_VEX_ESSENCE_CHANCE) dropItem(ItemRegistry.vexEssence, 2)

            // always roll extra at full moon
            if (isFullMoon()) if (random.nextDouble() < DROP_VEX_ESSENCE_CHANCE) dropItem(ItemRegistry.vexEssence, 2)
        }
        if (!getInventory().getStack(0).isEmpty) return
        if (random.nextDouble() > DROP_VEX_ESSENCE_CHANCE) return
        dropItem(ItemRegistry.vexEssence, 2)
    }

    private fun isNight(): Boolean {
        return this.world.timeOfDay in 13000 until 23216
    }

    private fun isFullMoon(): Boolean {
        return this.world.moonSize >= 0.99
    }

    private fun validateSynthesis() {
        if (world.isClient) return
        if (getSynthesisProgress().compareTo(MAX_PROGRESS) != 0) return
        setProgress(0)
        var synthesisStack = getInventory().getStack(2)
        if (synthesisStack.isEmpty) { // if this is the first synthesis output
            synthesisStack = getSynthesisItem().copy()
            synthesisStack.count = 1
        } else {
            if (synthesisStack.count < synthesisStack.maxCount) { // if this is not the first and the slot is not overflown
                synthesisStack.increment(1)
            } else { // if the slot is overflown
                val dropStack = synthesisStack.copy()
                dropStack.count = 1
                dropStack(dropStack)
            }
        }
        playSound(getSynthesisSound(), 1f, 0.8f + world.random.nextFloat() / 10 * 4) // 1.0f +- 0.2f

        getInventory().setStack(2, synthesisStack)
        getInventory().markDirty()
    }


    private fun synthesisItem() {
        if (world.isClient) return
        if (getSynthesisItem().isEmpty) {
            spellCastingPoseTick = 0
            return
        }

        if (spellCastingPoseTick > 0) {
            spellCastingPoseTick--
        }

        if (world.random.nextFloat() < SYNTHESIS_CHANCE) incrementProgress()

        if (!isSpellCasting() && !getInventory().getStack(1).isEmpty) {
            castSpell()

            getInventory().getStack(1).decrement(1)
            getInventory().markDirty()
        }

        if (!isSpellCasting() && world.random.nextFloat() < SYNTHESIS_CAN_SPEED_UP_CHANCE) castSpell()

        if (!isSpellCasting()) return

        world.random.doubles(SYNTHESIS_SPEED_UP_COUNT).forEach {
            if (it < SYNTHESIS_SPEED_UP_CHANCE) incrementProgress()
        }
    }

    private fun castSpell() {
        spellCastingPoseTick = MAX_SPELL_TICK
        playSpellCastingEffect()
    }

    override fun handleStatus(status: Byte) {
        if (status == SPELLCASTING_STATUS_NUMBER) {
            playSpellCastingEffect()
        } else {
            super.handleStatus(status)
        }
    }

    private fun playSpellCastingEffect() {
        if (world is ServerWorld) {
            world.sendEntityStatus(this, SPELLCASTING_STATUS_NUMBER)
        } else {
            for (i in 0 until 20) {
                val particleX = blockPos.x + random.nextDouble()
                val particleY = this.randomBodyY - random.nextGaussian() * 0.02
                val particleZ = blockPos.z + random.nextDouble()
                world.addParticle(
                    ParticleTypes.ENTITY_EFFECT,
                    particleX,
                    particleY,
                    particleZ,
                    255.0, // is now color red
                    80.0, // is now color green
                    80.0 // is now color blue
                )
            }
        }
    }

    private fun validatePose() {
        if (spellCastingPoseTick > 0) {
            setState(State.SPELL_CASTING)
        } else {
            setState(State.CROSSED)
        }
    }

    private fun isSpellCasting(): Boolean {
        return spellCastingPoseTick > 0
    }

    private fun incrementProgress() {
        setProgress((getSynthesisProgress() + 1))
    }

    fun getInventory(): Inventory {
        return inventory
    }

    enum class State {
        CROSSED, SPELL_CASTING;

        companion object {
            val TRACKED_STATE = object : TrackedDataHandler<State> {
                override fun write(data: PacketByteBuf, state: State) {
                    data.writeEnumConstant(state)
                }

                override fun copy(state: State): State {
                    return state
                }

                override fun read(packetByteBuf: PacketByteBuf): State {
                    return packetByteBuf.readEnumConstant(State::class.java)
                }

            }

            fun init() {
                TrackedDataHandlerRegistry.register(TRACKED_STATE)
            }
        }
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        val interactMob = super.interactMob(player, hand)
        if (interactMob == ActionResult.PASS || interactMob.isAccepted) {
            speakWith(player)
            return ActionResult.success(this.world.isClient)
        }
        return interactMob
    }

    private fun speakWith(player: PlayerEntity) {
        if (currentUser != null) return
        currentUser = player
        player.openHandledScreen(guiFactory)
    }

    fun endInteraction() {
        currentUser = null
    }

    class EvioneScreenFactory(private val evione: Evione) : NamedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
            return EvioneServerScreenHandler(
                Screens.EVIONE_SCREEN.screenHandlerType,
                syncId,
                inv,
                evione
            )
        }

        override fun getDisplayName(): Text {
            return TranslatableText(Screens.EVIONE_SCREEN.translationKey)
        }
    }

    class EvioneWanderAroundGoal(pathAwareEntity: Evione, speed: Double, chance: Int, bl: Boolean) :
        WanderAroundGoal(pathAwareEntity, speed, chance, bl) {

        override fun canStart(): Boolean {
            if ((mob as Evione).currentUser != null) return false
            return super.canStart()
        }
    }

    class EvioneInventory(private val who: Evione) : Inventory {

        private var items = DefaultedList.ofSize(3, ItemStack.EMPTY)

        override fun markDirty() {
            // does nothing
        }

        override fun clear() {
            items.clear()
            who.resetSynthesis()
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            items[slot] = stack
            if (slot != 0) return
            who.resetSynthesis()
            who.equipStack(EquipmentSlot.MAINHAND, stack)
        }

        override fun isEmpty(): Boolean {
            return IntStream.range(0, 3).allMatch {
                items[it].isEmpty
            }
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return items[slot].split(amount)
        }

        override fun removeStack(slot: Int): ItemStack {
            val ret = items[slot]
            items[slot] = ItemStack.EMPTY
            return ret
        }

        override fun getStack(slot: Int): ItemStack {
            return items[slot]
        }

        override fun canPlayerUse(player: PlayerEntity): Boolean = true

        override fun size(): Int = 3

        fun toList(): DefaultedList<ItemStack> {
            return items
        }

        fun load(list: DefaultedList<ItemStack>) {
            items = list
        }
    }
}