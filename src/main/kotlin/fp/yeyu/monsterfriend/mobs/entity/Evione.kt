package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.screens.EvioneGUI
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.function.Predicate

class Evione(
    entityType: EntityType<out PathAwareEntity>?,
    world: World?
) : PathAwareEntity(entityType, world) {

    companion object {
        val STATE: TrackedData<Byte> = DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)
        fun createEvioneAttributes(): DefaultAttributeContainer.Builder {
            return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
        }
    }

    fun setState(state: State) {
        this.dataTracker.set(STATE, State[state])
    }

    fun getState(): State {
        return State[this.dataTracker.get(STATE)]
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(STATE, State[State.CROSSED])
    }

    override fun initGoals() {
        super.initGoals()

        goalSelector.add(4, WanderAroundGoal(this, 0.6))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(2, WanderAroundPointOfInterestGoal(this, 0.6, false))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))

        targetSelector.add(
            3, FollowTargetGoal<MobEntity>(this,
                MobEntity::class.java, 5, false, false,
                Predicate<LivingEntity> { livingEntity: LivingEntity -> livingEntity is Monster && livingEntity !is CreeperEntity }
            )
        )
    }

    override fun mobTick() {
        super.mobTick()
        validateState()
    }

    private fun validateState() {
        if (getState() != State.CROSSED) {
            setState(State.CROSSED)
        }
    }

    fun getInventory(): Inventory {
        return inventory
    }

    private var currentInteraction: PlayerEntity? = null
    private val inventory = SimpleInventory(2)
    private val guiHandler = EvioneGuiHandler(this)

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
            return EvioneGUI(syncId, inv, ScreenHandlerContext.create(player.world, player.blockPos), evione)
        }

        override fun getDisplayName(): Text {
            return TranslatableText("container.friendlymob.evione")
        }

    }
}