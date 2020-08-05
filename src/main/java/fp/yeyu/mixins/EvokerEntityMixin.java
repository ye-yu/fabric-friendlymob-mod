package fp.yeyu.mixins;

import fp.yeyu.mixinutil.Transformer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EvokerEntity.class)
public abstract class EvokerEntityMixin extends IllagerEntity implements Transformer {

    protected EvokerEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @NotNull
    @Override
    public MobEntity getEntity() {
        return this;
    }

}
