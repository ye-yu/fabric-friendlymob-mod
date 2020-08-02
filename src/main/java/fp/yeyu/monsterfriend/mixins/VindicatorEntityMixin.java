package fp.yeyu.monsterfriend.mixins;

import fp.yeyu.monsterfriend.mixinutil.Transformer;
import fp.yeyu.monsterfriend.mobs.MobRegistry;
import fp.yeyu.monsterfriend.utils.ConfigFile;
import io.github.yeyu.util.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VindicatorEntity.class)
public abstract class VindicatorEntityMixin extends IllagerEntity implements Transformer {
    protected VindicatorEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.world instanceof ServerWorld)) return;
        final ServerWorld serverWorld = (ServerWorld) this.world;
        final HitResult blockHit = this.rayTrace(20, 0, false);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            final Vec3d pos = blockHit.getPos();
            final BlockState block = serverWorld.getBlockState(new BlockPos(pos));
            if (block.getBlock() == Blocks.EMERALD_BLOCK) {
                if (this.random.nextFloat() < ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.VINDOR_TRANSFORM_CHANCE)) {
                    if (this.transformTo(MobRegistry.INSTANCE.getVindor().getEntityType())) {
                        Logger.INSTANCE.info("Spawned a vindor");
                    } else {
                        Logger.INSTANCE.error("Cannot spawn a vindor!", new Throwable());
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    public MobEntity getEntity() {
        return this;
    }
}
