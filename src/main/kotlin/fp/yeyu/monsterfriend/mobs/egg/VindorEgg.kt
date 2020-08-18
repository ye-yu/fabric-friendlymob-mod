package fp.yeyu.monsterfriend.mobs.egg

import fp.yeyu.monsterfriend.mobs.MobRegistry
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.SpawnEggItem
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class VindorEgg(type: EntityType<*>, primaryColor: Int, secondaryColor: Int, settings: Settings) :
    SpawnEggItem(type, primaryColor, secondaryColor, settings) {
    companion object {
        const val NAME = "vindor_egg"
        val SPAWN_ENTITY = MobRegistry.vindor.entityType
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        return EggUtil.useOnBlock(context, SPAWN_ENTITY, EntityType.VINDICATOR)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack>? {
        return EggUtil.use(this, world, user, hand, SPAWN_ENTITY)
    }

}