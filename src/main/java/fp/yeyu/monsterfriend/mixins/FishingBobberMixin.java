package fp.yeyu.monsterfriend.mixins;

import fp.yeyu.monsterfriend.item.ItemRegistry;
import fp.yeyu.monsterfriend.mixinutil.Transformer;
import fp.yeyu.monsterfriend.mobs.MobRegistry;
import fp.yeyu.monsterfriend.utils.ConfigFile;
import io.github.yeyu.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin {

    @Inject(method = "pullHookedEntity", at = @At("HEAD"))
    public void pullEvioneEntityCheck(CallbackInfo ci) {
        final FishingBobberEntity bobberEntity = ((FishingBobberEntity) (Object) this);
        if (bobberEntity.world.isClient) return;
        final Entity hookedEntity = bobberEntity.getHookedEntity();
        final PlayerEntity playerEntity = bobberEntity.getOwner();
        if (hookedEntity == null) return;
        if (playerEntity == null) return;
        if (!(hookedEntity instanceof EvokerEntity)) return;
        if (playerEntity.getRandom().nextFloat() >= ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.VEX_ESSENCE_CAUGHT_CHANCE))
            return;

        // passed chance to get a vex essence
        ItemStack itemStack = new ItemStack(ItemRegistry.INSTANCE.getVexEssence());
        ItemEntity itemEntity = new ItemEntity(bobberEntity.world, bobberEntity.getX(), bobberEntity.getY(), bobberEntity.getZ(), itemStack);
        double d = playerEntity.getX() - bobberEntity.getX();
        double e = playerEntity.getY() - bobberEntity.getY();
        double f = playerEntity.getZ() - bobberEntity.getZ();
        double g = 0.1D;
        itemEntity.setVelocity(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);
        bobberEntity.world.spawnEntity(itemEntity);
        playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.getX(), playerEntity.getY() + 0.5D, playerEntity.getZ() + 0.5D, playerEntity.getRandom().nextInt(6) + 1));

        if (playerEntity.getRandom().nextFloat() >= ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.EVIONE_TRANSFORM_CHANCE))
            return;

        // passed chance to transform entity to evione
        Transformer evoker = (Transformer) hookedEntity;
        if (evoker.transformTo(MobRegistry.INSTANCE.getEvione().getEntityType())) {
            Logger.INSTANCE.info("Spawned an evione.");
            playerEntity.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 0.8f + playerEntity.world.random.nextFloat() / 10 * 4); // 1.0f +- 0.2f
        } else {
            Logger.INSTANCE.error("Cannot spawn an evione!", new Throwable());
        }
    }

}
