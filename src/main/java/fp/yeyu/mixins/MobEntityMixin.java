package fp.yeyu.mixins;

import fp.yeyu.util.Transformable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity implements Transformable {
    public MobEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    @Nullable
    protected abstract <T extends MobEntity> T method_29243(EntityType<T> entityType);

    @Override
    public <T extends MobEntity> MobEntity transformTo(EntityType<T> to) {
        return this.method_29243(to);
    }

    @Inject(method = "playSpawnEffects", at = @At("HEAD"), cancellable = true)
    public void playSpawnEffectsMixin(CallbackInfo ci) {
        if (world.isClient) {
            IntStream.range(0, 20).forEach((i) -> {
                double particleX = getBlockPos().getX() + random.nextDouble();
                double particleY = getRandomBodyY() - random.nextGaussian() * 0.02;
                double particleZ = getBlockPos().getZ() + random.nextDouble();
                world.addParticle(
                        ParticleTypes.POOF,
                        particleX,
                        particleY,
                        particleZ,
                        0,
                        0,
                        0
                );
            });
            ci.cancel();
        }
    }
}
