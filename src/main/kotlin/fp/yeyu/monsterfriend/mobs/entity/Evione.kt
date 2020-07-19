package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import java.util.*
import java.util.function.Predicate

class Evione(
    entityType: EntityType<out PathAwareEntity>?,
    world: World?
) : PathAwareEntity(entityType, world), Angerable {

    private object AngryStruct {
        var currentAngerTicks = -1
        var angryAt: UUID? = null
        const val ANGER_DURATION = 50
    }

    companion object {
        const val NAME: String = "evione"
        val STATE: TrackedData<Byte> = DataTracker.registerData(Evione::class.java, TrackedDataHandlerRegistry.BYTE)
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

    override fun getAngerTime(): Int {
        return AngryStruct.currentAngerTicks
    }

    override fun setAngerTime(ticks: Int) {
        AngryStruct.currentAngerTicks = ticks
    }

    override fun chooseRandomAngerTime() {
        AngryStruct.currentAngerTicks = AngryStruct.ANGER_DURATION
    }

    override fun getAngryAt(): UUID? {
        return AngryStruct.angryAt
    }

    override fun setAngryAt(uuid: UUID?) {
        AngryStruct.angryAt = uuid
    }

    override fun initGoals() {
        super.initGoals()

        goalSelector.add(4, WanderAroundGoal(this, 0.6))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(2, WanderAroundPointOfInterestGoal(this, 0.6, false))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))

        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(
            3, FollowTargetGoal<PlayerEntity>(this,
                PlayerEntity::class.java, 10, true, false,
                Predicate<LivingEntity> { entity: LivingEntity -> shouldAngerAt(entity) }
            )
        )
        targetSelector.add(
            3, FollowTargetGoal<MobEntity>(this,
                MobEntity::class.java, 5, false, false,
                Predicate<LivingEntity> { livingEntity: LivingEntity -> livingEntity is Monster && livingEntity !is CreeperEntity }
            )
        )
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun mobTick() {
        super.mobTick()
        validateState()
    }

    private fun validateState() {
        if (hasAngerTime() && getState() != State.SPELL_CASTING) {
            setState(State.SPELL_CASTING)
        } else if (!hasAngerTime() && getState() != State.CROSSED) {
            setState(State.CROSSED)
        }
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
}