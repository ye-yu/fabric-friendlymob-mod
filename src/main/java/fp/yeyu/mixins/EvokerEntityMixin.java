package fp.yeyu.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EvokerEntity.class)
public abstract class EvokerEntityMixin extends IllagerEntity {

    protected EvokerEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }
}
