package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.screens.EvioneScreenDescription
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
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class Evione(
    entityType: EntityType<out PathAwareEntity>?,
    world: World?
) : PathAwareEntity(entityType, world) {

    private var spellCastingPoseTick: Int = -1
    private var currentInteraction: PlayerEntity? = null
    private val inventory = SimpleInventory(1)
    private val guiHandler = EvioneGuiHandler(this)

    init {
        setSynthesisItem(ItemStack.EMPTY)
        getInventory().setStack(0, ItemStack.EMPTY)
    }
    companion object {
        val POSE_STATE: TrackedData<Byte> =
            DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)
        val SYNTHESIS_PROGRESS: TrackedData<Byte> =
            DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)
        const val MAX_PROGRESS = 100

        const val POSE_STATE_NAME = "pose_state"

        const val SYNTHESIS_PROGRESS_NAME = "synthesis_progress"

        fun createEvioneAttributes(): DefaultAttributeContainer.Builder {
            return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
        }
        fun isEssence(item: Item): Boolean {
            return item == Items.EXPERIENCE_BOTTLE
        }
        private const val MAX_SPELL_TICK: Int = 50
        private const val SYNTHESIS_CHANCE = 0.05f
        private const val SYNTHESIS_CAN_SPEED_UP_CHANCE = 0.03f

        private const val SYNTHESIS_SPEED_UP_CHANCE = 0.03f

    }

    fun synthesisNewItem(itemStack: ItemStack) {
        setProgress(0)
        setSynthesisItem(itemStack)
    }

    fun clearSynthesisItem() {
        synthesisNewItem(ItemStack.EMPTY)
    }

    fun setSynthesisItem(itemStack: ItemStack) {
        itemStack.count = 1
        equipStack(EquipmentSlot.MAINHAND, itemStack)
    }
    fun setProgress(p: Byte) {
        val clamped = max(0, min(MAX_PROGRESS, p.toInt()))
        LOGGER.info("Set new progress to $clamped")
        this.dataTracker.set(SYNTHESIS_PROGRESS, clamped.toByte())
        if (world !is ServerWorld) return
        val screenHandler = currentInteraction?.currentScreenHandler ?: return
        (screenHandler as EvioneScreenDescription).sendProgressToClient(clamped)
    }
    private fun getSynthesisSound(): SoundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_AMBIENT
    override fun getFallSound(distance: Int): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT

    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_EVOKER_HURT

    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_EVOKER_DEATH

    fun setState(state: State) {
        this.dataTracker.set(POSE_STATE, State[state])
    }

    fun getState(): State {
        return State[this.dataTracker.get(POSE_STATE)]
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(POSE_STATE, State[State.CROSSED])
        this.dataTracker.startTracking(SYNTHESIS_PROGRESS, 0)
    }

    override fun writeCustomDataToTag(tag: CompoundTag) {
        super.writeCustomDataToTag(tag)
        tag.putByte(POSE_STATE_NAME, State[getState()])
        tag.putByte(SYNTHESIS_PROGRESS_NAME, getSynthesisProgress())
    }

    override fun readCustomDataFromTag(tag: CompoundTag) {
        super.readCustomDataFromTag(tag)
        if (tag.contains(POSE_STATE_NAME)) setState(State[tag.getByte(POSE_STATE_NAME)])
        if (tag.contains(SYNTHESIS_PROGRESS_NAME)) setProgress(tag.getByte(SYNTHESIS_PROGRESS_NAME))
    }

    fun getSynthesisProgress(): Byte {
        return this.dataTracker[SYNTHESIS_PROGRESS]
    }

    fun getSynthesisItem(): ItemStack {
        return getEquippedStack(EquipmentSlot.MAINHAND)
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
    }

    private fun synthesisItem() {
        if (world.isClient) return
        if (getSynthesisItem().isEmpty) return
        if (world.random.nextFloat() < SYNTHESIS_CHANCE) incrementProgress()
        if (world.random.nextFloat() < SYNTHESIS_CAN_SPEED_UP_CHANCE) castSpell()
        if (!isSpellCasting()) return
        world.random.doubles(3).forEach {
            if (it < SYNTHESIS_SPEED_UP_CHANCE) incrementProgress()
        }

        if (getSynthesisProgress().compareTo(MAX_PROGRESS) == 0) {
            var synthesisStack = getInventory().getStack(0)
            if (synthesisStack == ItemStack.EMPTY) {
                synthesisStack = getSynthesisItem().copy()
            } else {
                if (synthesisStack.count >= synthesisStack.maxCount) {
                    val dropStack = synthesisStack.copy()
                    dropStack.count = 1
                    dropStack(dropStack)
                } else {
                    synthesisStack.increment(1)
                }
            }
            getInventory().setStack(0, synthesisStack)
            playSound(getSynthesisSound(), 1f, 0.8f + world.random.nextFloat() / 10 * 4) // 1.0f +- 0.2f
        }
    }

    private fun castSpell() {
        spellCastingPoseTick = MAX_SPELL_TICK
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

    override fun interactMob(player: PlayerEntity?, hand: Hand?): ActionResult {
        val interactMob = super.interactMob(player, hand)
        if (interactMob == ActionResult.PASS || interactMob.isAccepted) {
            speakWith(player!!)
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
            return EvioneScreenDescription(
                syncId,
                inv,
                ScreenHandlerContext.create(player.world, player.blockPos),
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
}