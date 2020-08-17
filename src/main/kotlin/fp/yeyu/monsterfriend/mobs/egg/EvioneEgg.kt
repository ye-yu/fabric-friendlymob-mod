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

class EvioneEgg(type: EntityType<*>, primaryColor: Int, secondaryColor: Int, settings: Settings) :
    SpawnEggItem(type, primaryColor, secondaryColor, settings) {
    companion object {
        const val NAME = "evione_egg"
        val SPAWN_ENTITY = MobRegistry.evione.entityType!!
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        return EggUtil.useOnBlock(context, SPAWN_ENTITY, EntityType.EVOKER)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack>? {
        return EggUtil.use(this, world, user, hand, SPAWN_ENTITY)
    }

}