package fp.yeyu.mcvisualmod.mobs.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.FollowTargetGoal
import net.minecraft.entity.ai.goal.LookAroundGoal
import net.minecraft.entity.ai.goal.RevengeGoal
import net.minecraft.entity.mob.VindicatorEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Vindor(entityType: EntityType<out VindicatorEntity>?, world: World?) : VindicatorEntity(entityType, world) {

    companion object {
        val NAME = "vindor"
        val LOGGER: Logger = LogManager.getLogger()
    }

    override fun initGoals() {
//        super.initGoals()
        goalSelector.add(0, LookAroundGoal(this))
        targetSelector.add(1, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(
            2, FollowTargetGoal(
                this,
                PlayerEntity::class.java, true
            )
        )
    }

    override fun interactMob(player: PlayerEntity?, hand: Hand?): ActionResult {
        val interactMob = super.interactMob(player, hand)
        if (interactMob == ActionResult.PASS || interactMob.isAccepted) {
            LOGGER.info(String.format("%s is interacting with a Vindor.", player?.displayName))
            return ActionResult.SUCCESS
        }
        return interactMob
    }
}