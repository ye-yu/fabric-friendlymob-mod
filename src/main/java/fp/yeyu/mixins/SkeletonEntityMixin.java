package fp.yeyu.mixins;

import fp.yeyu.monsterfriend.mobs.MobRegistry;
import fp.yeyu.monsterfriend.mobs.entity.Wizard;
import fp.yeyu.monsterfriend.mobs.entity.wizard.WizardProfessionCollection;
import fp.yeyu.util.Transformable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends AbstractSkeletonEntity implements Transformable {
    protected SkeletonEntityMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        final ItemStack stackInHand = player.getStackInHand(hand);
        stackInHand.decrement(1);
        if (random.nextFloat() > 0.15f) return super.interactMob(player, hand);
        final MobEntity skelly = transformTo(MobRegistry.INSTANCE.getSkelly().getEntityType());
        skelly.playSpawnEffects();
        skelly.playSound(SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1f, 0.8f + world.random.nextFloat() / 10 * 4); // 1.0f +- 0.2f
        return super.interactMob(player, hand);
    }
}
