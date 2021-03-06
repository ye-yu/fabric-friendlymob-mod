package fp.yeyu.mixins;

import fp.yeyu.monsterfriend.mobs.MobRegistry;
import fp.yeyu.monsterfriend.mobs.entity.Wizard;
import fp.yeyu.monsterfriend.mobs.entity.wizard.WizardProfessionCollection;
import fp.yeyu.util.DislikeParticlePlayer;
import fp.yeyu.util.Transformable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity implements Transformable, DislikeParticlePlayer {

    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        final ItemStack stackInHand = player.getStackInHand(hand);
        stackInHand.decrement(1);
        if (!WizardProfessionCollection.INSTANCE.getProfessionMap().containsKey(stackInHand.getItem()))
            return super.interactMob(player, hand);
        if (random.nextFloat() > 0.15f) {
            playDislikeParticle();
            return super.interactMob(player, hand);
        }
        final MobEntity mobEntity = transformTo(MobRegistry.INSTANCE.getWizard().getEntityType());
        mobEntity.playSpawnEffects();
        final ItemStack flower = stackInHand.copy();
        flower.setCount(1);
        ((Wizard) mobEntity).setFlower(flower);
        ((Wizard) mobEntity).makeNewCraft();
        mobEntity.playSound(SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1f, 0.8f + world.random.nextFloat() / 10 * 4); // 1.0f +- 0.2f
        return super.interactMob(player, hand);
    }
}
