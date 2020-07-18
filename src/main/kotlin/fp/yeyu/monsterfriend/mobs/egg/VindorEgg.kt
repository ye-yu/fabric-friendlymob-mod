package fp.yeyu.monsterfriend.mobs.egg

import fp.yeyu.monsterfriend.BefriendMinecraft
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.MobSpawnerBlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.SpawnEggItem
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.RayTraceContext
import net.minecraft.world.World

class VindorEgg(type: EntityType<*>?, primaryColor: Int, secondaryColor: Int, settings: Settings?) :
    SpawnEggItem(type, primaryColor, secondaryColor, settings) {
    companion object {
        val NAME = "vindor_egg"
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        val world = context.world
        return if (world.isClient) {
            ActionResult.SUCCESS
        } else {
            val itemStack = context.stack
            val blockPos = context.blockPos
            val direction = context.side
            val blockState = world.getBlockState(blockPos)
            if (blockState.isOf(Blocks.SPAWNER)) {
                val blockEntity = world.getBlockEntity(blockPos)
                if (blockEntity is MobSpawnerBlockEntity) {
                    val mobSpawnerLogic = blockEntity.logic
                    val entityType = EntityType.VINDICATOR // always back to vindicator
                    mobSpawnerLogic.setEntityId(entityType)
                    blockEntity.markDirty()
                    world.updateListeners(blockPos, blockState, blockState, 3)
                    itemStack.decrement(1)
                    return ActionResult.CONSUME
                }
            }
            val blockPos3: BlockPos
            blockPos3 = if (blockState.getCollisionShape(world, blockPos).isEmpty) {
                blockPos
            } else {
                blockPos.offset(direction)
            }
            val entityType2 = getEntityType(itemStack.tag)
            if (entityType2.spawnFromItemStack(
                    world,
                    itemStack,
                    context.player,
                    blockPos3,
                    SpawnReason.SPAWN_EGG,
                    true,
                    blockPos != blockPos3 && direction == Direction.UP
                ) != null
            ) {
                itemStack.decrement(1)
            }
            ActionResult.CONSUME
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack>? {
        val itemStack = user.getStackInHand(hand)
        val hitResult: HitResult =
            Item.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY)
        return if (hitResult.type != HitResult.Type.BLOCK) {
            TypedActionResult.pass(itemStack)
        } else if (world.isClient) {
            TypedActionResult.success(itemStack)
        } else {
            val blockHitResult = hitResult as BlockHitResult
            val blockPos = blockHitResult.blockPos
            if (world.getBlockState(blockPos).block !is FluidBlock) {
                TypedActionResult.pass(itemStack)
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(
                    blockPos,
                    blockHitResult.side,
                    itemStack
                )
            ) {
                val entityType = BefriendMinecraft.Mobs.VINDOR.entry
                if (entityType.spawnFromItemStack(
                        world,
                        itemStack,
                        user,
                        blockPos,
                        SpawnReason.SPAWN_EGG,
                        false,
                        false
                    ) == null
                ) {
                    TypedActionResult.pass(itemStack)
                } else {
                    if (!user.abilities.creativeMode) {
                        itemStack.decrement(1)
                    }
                    user.incrementStat(Stats.USED.getOrCreateStat(this))
                    TypedActionResult.consume(itemStack)
                }
            } else {
                TypedActionResult.fail(itemStack)
            }
        }
    }

}