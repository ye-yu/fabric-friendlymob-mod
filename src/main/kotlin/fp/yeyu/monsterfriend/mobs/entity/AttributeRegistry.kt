package fp.yeyu.monsterfriend.mobs.entity

import com.google.common.collect.Maps
import fp.yeyu.monsterfriend.BefriendMinecraft
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.mob.VindicatorEntity

class AttributeRegistry {
    companion object {
        val ATTRS: HashMap<EntityType<out LivingEntity>, DefaultAttributeContainer> = Maps.newHashMap()

        init {
            ATTRS[BefriendMinecraft.Mobs.VINDOR.entry] = VindicatorEntity.createVindicatorAttributes().build()
        }
    }
}