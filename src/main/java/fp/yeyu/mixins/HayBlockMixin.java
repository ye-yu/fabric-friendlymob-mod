package fp.yeyu.mixins;

import fp.yeyu.monsterfriend.item.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HayBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HayBlock.class)
public class HayBlockMixin extends PillarBlock {
    public HayBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() != Items.SHEARS) return super.onUse(state, world, pos, player, hand, hit);
        if (world instanceof ClientWorld) {
            return ActionResult.CONSUME;
        }

        if (world instanceof ServerWorld) {
            world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
            final ItemEntity itemEntity = createStrawHat(world, pos);
            itemEntity.setVelocity(itemEntity.getVelocity().add((world.random.nextFloat() - world.random.nextFloat()) * 0.1F, world.random.nextFloat() * 0.05F, (world.random.nextFloat() - world.random.nextFloat()) * 0.1F));
            stack.damage(1, player, (playerEntity) -> playerEntity.sendToolBreakStatus(hand));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    private ItemEntity createStrawHat(World world, BlockPos pos) {
        final ItemStack stack = new ItemStack(ItemRegistry.INSTANCE.getStrawHat());
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + (double) 1, pos.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
        return itemEntity;
    }
}
