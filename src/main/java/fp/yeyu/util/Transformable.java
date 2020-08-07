package fp.yeyu.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

public interface Transformable {
    <T extends MobEntity> MobEntity transformTo(EntityType<T> to);
}
