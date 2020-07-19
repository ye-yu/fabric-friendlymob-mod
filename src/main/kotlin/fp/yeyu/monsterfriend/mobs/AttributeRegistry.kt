package fp.yeyu.monsterfriend.mobs

import com.google.common.collect.Maps
import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.mobs.entity.Vindor
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.mob.MobEntity

object AttributeRegistry {
    val attributes: HashMap<EntityType<out LivingEntity>?, DefaultAttributeContainer> = Maps.newHashMap()

    init {
        attributes[MobRegistry.vindor.entityType] = Vindor.createVindorAttributes().build()
        attributes[MobRegistry.evione.entityType] = Evione.createEvioneAttributes().build()
        attributes[null] = MobEntity.createMobAttributes().build()
    }
}