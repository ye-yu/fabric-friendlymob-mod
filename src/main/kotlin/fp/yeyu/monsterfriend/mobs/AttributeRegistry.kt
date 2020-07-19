package fp.yeyu.monsterfriend.mobs

import com.google.common.collect.Maps
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.mob.EvokerEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.VindicatorEntity

object AttributeRegistry {
    val attributes: HashMap<EntityType<out LivingEntity>?, DefaultAttributeContainer> = Maps.newHashMap()

    init {
        attributes[MobRegistry.vindor.entityType] = VindicatorEntity.createVindicatorAttributes().build()
        attributes[MobRegistry.evione.entityType] = EvokerEntity.createEvokerAttributes().build()
        attributes[null] = MobEntity.createMobAttributes().build()
    }
}