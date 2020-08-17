package fp.yeyu.monsterfriend.mobs.egg

import fp.yeyu.monsterfriend.mobs.MobRegistry
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.MobSpawnerBlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
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
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RayTraceContext
import net.minecraft.world.World

object EggUtil {

    fun useOnBlock(context: ItemUsageContext, spawnEntity: EntityType<out MobEntity>, fallbackMob: EntityType<out MobEntity>): ActionResult {
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
                    mobSpawnerLogic.setEntityId(fallbackMob)
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
            if (spawnEntity.spawnFromItemStack(
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

    fun use(
        context: SpawnEggItem,
        world: World,
        user: PlayerEntity,
        hand: Hand?,
        entityType: EntityType<out MobEntity>
    ): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        val hitResult: HitResult = rayTrace(world, user)
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
                    user.incrementStat(Stats.USED.getOrCreateStat(context))
                    TypedActionResult.consume(itemStack)
                }
            } else {
                TypedActionResult.fail(itemStack)
            }
        }
    }

    private fun rayTrace(world: World, user: PlayerEntity): HitResult {
        val pitch: Float = user.pitch
        val yaw: Float = user.yaw
        val vec3d: Vec3d = user.getCameraPosVec(1.0f)
        val h = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f)
        val i = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f)
        val j = -MathHelper.cos(-pitch * 0.017453292f)
        val k = MathHelper.sin(-pitch * 0.017453292f)
        val l = i * j
        val n = h * j
        val d = 5.0
        val vec3d2 = vec3d.add(l.toDouble() * d, k.toDouble() * d, n.toDouble() * d)
        return world.rayTrace(
            RayTraceContext(
                vec3d,
                vec3d2,
                RayTraceContext.ShapeType.OUTLINE,
                RayTraceContext.FluidHandling.SOURCE_ONLY,
                user
            )
        )

    }

}