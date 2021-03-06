package fp.yeyu.mixins;

import fp.yeyu.monsterfriend.item.ItemRegistry;
import fp.yeyu.monsterfriend.mobs.MobRegistry;
import fp.yeyu.monsterfriend.utils.ConfigFile;
import fp.yeyu.util.Transformable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin {


    @Shadow
    private Entity hookedEntity;

    @Inject(method = "pullHookedEntity", at = @At("HEAD"), cancellable = true)
    public void pullEvioneEntityCheck(CallbackInfo ci) {
        final FishingBobberEntity bobberEntity = ((FishingBobberEntity) (Object) this);
        if (bobberEntity.world.isClient) return;
        final Entity bobberEntityHookedEntity = bobberEntity.getHookedEntity();
        final PlayerEntity playerEntity = bobberEntity.getOwner();
        if (bobberEntityHookedEntity == null) return;
        if (playerEntity == null) return;
        if (!(bobberEntityHookedEntity instanceof EvokerEntity)) return;
        if (playerEntity.getRandom().nextFloat() >= ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.VEX_ESSENCE_CAUGHT_CHANCE))
            return;

        // passed chance to get a vex essence
        ItemStack itemStack = new ItemStack(ItemRegistry.INSTANCE.getVexEssence());
        ItemEntity itemEntity = new ItemEntity(bobberEntity.world, bobberEntity.getX(), bobberEntity.getY(), bobberEntity.getZ(), itemStack);
        double velX = playerEntity.getX() - bobberEntity.getX();
        double velY = playerEntity.getY() - bobberEntity.getY();
        double velZ = playerEntity.getZ() - bobberEntity.getZ();
        itemEntity.setVelocity(velX * 0.1D, velY * 0.1D + Math.sqrt(Math.sqrt(velX * velX + velY * velY + velZ * velZ)) * 0.08D, velZ * 0.1D);
        bobberEntity.world.spawnEntity(itemEntity);
        playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.getX(), playerEntity.getY() + 0.5D, playerEntity.getZ() + 0.5D, playerEntity.getRandom().nextInt(6) + 1));

        if (playerEntity.getRandom().nextFloat() >= ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.EVIONE_TRANSFORM_CHANCE))
            return;

        final MobEntity evione = ((Transformable) bobberEntityHookedEntity).transformTo(MobRegistry.INSTANCE.getEvione().getEntityType());
        evione.playSpawnEffects();
        bobberEntityHookedEntity.playSound(SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1f, 0.8f + bobberEntityHookedEntity.world.random.nextFloat() / 10 * 4); // 1.0f +- 0.2f
    }

}
