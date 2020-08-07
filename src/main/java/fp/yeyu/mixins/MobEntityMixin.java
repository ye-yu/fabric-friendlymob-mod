package fp.yeyu.mixins;

import fp.yeyu.util.Transformable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

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
}
