package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity

class LookAtCustomerGoal<T>(private val mobEntity: T) :
    LookAtEntityGoal(mobEntity, PlayerEntity::class.java, 8f)
        where T : MobEntity, T : GuiProvider {

    override fun canStart(): Boolean {
        if (mobEntity.currentUser == null) return false
        return super.canStart()
    }
}