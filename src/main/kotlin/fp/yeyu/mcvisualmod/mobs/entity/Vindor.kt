package fp.yeyu.mcvisualmod.mobs.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate

class Vindor(entityType: EntityType<out IronGolemEntity>?, world: World?) : IronGolemEntity(entityType, world) {

    init {
        equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.IRON_AXE))
    }

    override fun canBeLeashedBy(player: PlayerEntity?): Boolean {
        return false
    }

    companion object {
        val NAME = "vindor"
        val LOGGER: Logger = LogManager.getLogger()
    }

    @Environment(EnvType.CLIENT)
    fun getState(): State? {
        return if (isAttacking) {
            State.ATTACKING
        } else {
            State.CROSSED
        }
    }

    @Environment(EnvType.CLIENT)
    enum class State {
        CROSSED, ATTACKING
    }

    var currentCustomer: PlayerEntity? = null

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(2, WanderAroundPointOfInterestGoal(this, 0.6, false))
//        goalSelector.add(4, IronGolemWanderAroundGoal(this, 0.6))
//        goalSelector.add(5, IronGolemLookGoal(this))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
//        targetSelector.add(1, TrackIronGolemTargetGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(
            3, FollowTargetGoal(this,
                PlayerEntity::class.java, 10, true, false,
                Predicate { entity -> shouldAngerAt(entity) }
            )
        )
        targetSelector.add(
            3, FollowTargetGoal(this,
                MobEntity::class.java, 5, false, false,
                Predicate { livingEntity -> livingEntity is Monster && livingEntity !is CreeperEntity }
            )
        )
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun interactMob(player: PlayerEntity?, hand: Hand?): ActionResult {
        val interactMob = super.interactMob(player, hand)
        if (interactMob == ActionResult.PASS || interactMob.isAccepted) {
            LOGGER.info(String.format("%s is interacting with a Vindor.", player?.displayName))
            trade(player)
            return ActionResult.SUCCESS
        }
        return interactMob
    }

    private fun trade(player: PlayerEntity?) {
        if (player == null) return

        if (currentCustomer != null) return

        currentCustomer = player
    }
}