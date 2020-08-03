package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.Particle
import fp.yeyu.monsterfriend.Particle.Particles
import fp.yeyu.monsterfriend.item.ItemRegistry
import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.screens.evione.EvioneServerScreenHandler
import fp.yeyu.monsterfriend.utils.ConfigFile
import io.github.yeyu.util.DrawerUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.EscapeDangerGoal
import net.minecraft.entity.ai.goal.LookAroundGoal
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
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
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

class Evione(
    entityType: EntityType<out PathAwareEntity>?,
    world: World?
) : PathAwareEntity(entityType, world) {

    private var spellCastingPoseTick: Int = -1
    private var currentInteraction: PlayerEntity? = null
    private val inventory = EvioneInventory(this)
    private val guiHandler = EvioneGuiHandler(this)

    companion object {
        val POSE_STATE: TrackedData<Byte> =
            DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)
        val SYNTHESIS_PROGRESS: TrackedData<Byte> =
            DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)

        val INVENTORY: MutableList<TrackedData<ItemStack>> = MutableList(3) {
            DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.ITEM_STACK)
        }

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

    }

    private fun resetSynthesis() {
        setProgress(0)
        val drop = getInventory().removeStack(2)
        if (drop.isEmpty) return
        dropStack(drop)
    }

    private fun setProgress(p: Byte) {
        val clamped = max(0, min(MAX_PROGRESS, p.toInt()))
        this.dataTracker.set(SYNTHESIS_PROGRESS, clamped.toByte())
    }

    private fun getSynthesisSound(): SoundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_AMBIENT
    override fun getFallSound(distance: Int): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT
    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT
    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_DEATH

    private fun setState(state: State) {
        this.dataTracker.set(POSE_STATE, State[state])
    }

    fun getState(): State {
        return State[this.dataTracker.get(POSE_STATE)]
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(POSE_STATE, State[State.CROSSED])
        this.dataTracker.startTracking(SYNTHESIS_PROGRESS, 0)
        INVENTORY.forEach {
            this.dataTracker.startTracking(it, ItemStack.EMPTY)
        }
    }

    override fun writeCustomDataToTag(tag: CompoundTag) {
        super.writeCustomDataToTag(tag)
        tag.putByte(POSE_STATE_NAME, State[getState()])
        tag.putByte(SYNTHESIS_PROGRESS_NAME, getSynthesisProgress())
        Inventories.toTag(tag, (getInventory() as EvioneInventory).toList())
    }

    override fun readCustomDataFromTag(tag: CompoundTag) {
        super.readCustomDataFromTag(tag)
        if (tag.contains(POSE_STATE_NAME)) setState(State[tag.getByte(POSE_STATE_NAME)])
        val list = DefaultedList.ofSize(3, ItemStack.EMPTY)
        Inventories.fromTag(tag, list)
        (getInventory() as EvioneInventory).load(list)

        // load last because setting inventory item resets progress
        if (tag.contains(SYNTHESIS_PROGRESS_NAME)) setProgress(tag.getByte(SYNTHESIS_PROGRESS_NAME))
    }

    fun getSynthesisProgress(): Byte {
        return this.dataTracker[SYNTHESIS_PROGRESS]
    }

    private fun getSynthesisItem(): ItemStack {
        return getInventory().getStack(0)
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(0, EscapeDangerGoal(this, 0.8))
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
        Particle.spawnParticle(this.world, this.blockPos, DrawerUtil.constructColor(0xFF, 0x50, 0x50, 0xFF), Particles.ENTITY)
        Particle.spawnParticle(this.world, this.blockPos.add(0, 1, 0), DrawerUtil.constructColor(0xFF, 0x50, 0x50, 0xFF), Particles.ENTITY)
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
        setProgress((getSynthesisProgress() + 1).toByte())
    }

    fun getInventory(): Inventory {
        return inventory
    }

    enum class State {
        CROSSED, SPELL_CASTING;

        companion object {
            operator fun get(index: Byte): State {
                return when (index) {
                    0.toByte() -> CROSSED
                    1.toByte() -> SPELL_CASTING
                    else -> throw IndexOutOfBoundsException(index.toString())
                }
            }

            operator fun get(state: State): Byte {
                return when (state) {
                    CROSSED -> 0
                    SPELL_CASTING -> 1
                }
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
        if (currentInteraction != null) return
        currentInteraction = player
        player.openHandledScreen(guiHandler)
    }

    fun endInteraction() {
        currentInteraction = null
    }

    class EvioneGuiHandler(private val evione: Evione) : NamedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
            return EvioneServerScreenHandler(
                Screens.EVIONE_SCREEN,
                syncId,
                inv,
                evione
            )
        }

        override fun getDisplayName(): Text {
            return TranslatableText("container.friendlymob.evione")
        }

    }


    class EvioneWanderAroundGoal(pathAwareEntity: Evione, speed: Double, chance: Int, bl: Boolean) :
        WanderAroundGoal(pathAwareEntity, speed, chance, bl) {

        override fun canStart(): Boolean {
            if ((mob as Evione).currentInteraction != null) return false
            return super.canStart()
        }
    }

    class EvioneInventory(private val who: Evione) : Inventory {

        override fun markDirty() {
            for (i in 0 until 3) {
                who.dataTracker[INVENTORY[i]] = getStack(i)
            }
        }

        override fun clear() {
            for (i in 0 until 3) {
                who.dataTracker[INVENTORY[i]] = ItemStack.EMPTY
            }
            who.resetSynthesis()
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            who.dataTracker[INVENTORY[slot]] = stack
            if (slot == 0) who.resetSynthesis()
            markDirty()
        }

        override fun isEmpty(): Boolean {
            return IntStream.range(0, 3).allMatch {
                who.dataTracker[INVENTORY[it]].isEmpty
            }
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            val item = who.dataTracker[INVENTORY[slot]]
            val split = item.split(amount)
            who.dataTracker[INVENTORY[slot]] = item
            return split
        }

        override fun removeStack(slot: Int): ItemStack {
            val ret = who.dataTracker[INVENTORY[slot]]
            who.dataTracker[INVENTORY[slot]] = ItemStack.EMPTY
            return ret
        }

        override fun getStack(slot: Int): ItemStack {
            return who.dataTracker[INVENTORY[slot]]
        }

        override fun canPlayerUse(player: PlayerEntity): Boolean = true

        override fun size(): Int = 3

        fun toList(): DefaultedList<ItemStack> {
            val list = DefaultedList.ofSize(3, ItemStack.EMPTY)
            for (i in 0 until 3) {
                list[i] = getStack(i)
            }
            return list
        }

        fun load(list: DefaultedList<ItemStack>) {
            list.indices.forEach {
                setStack(it, list[it])
            }
        }
    }
}