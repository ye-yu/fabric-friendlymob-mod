package fp.yeyu.mcvisualmod.mobs.entity

import com.google.common.collect.Maps
import fp.yeyu.mcvisualmod.SilentMinecraft
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.mob.VindicatorEntity

class AttributeRegistry {
    companion object {
        val ATTRS: HashMap<EntityType<out LivingEntity>, DefaultAttributeContainer> = Maps.newHashMap()

        init {
            ATTRS[SilentMinecraft.Mobs.VINDOR.entry] = VindicatorEntity.createVindicatorAttributes().build()
        }
    }
}