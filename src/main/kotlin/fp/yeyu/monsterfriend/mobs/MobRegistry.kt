package fp.yeyu.monsterfriend.mobs

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.mobs.renderer.EvioneRenderer
import fp.yeyu.monsterfriend.mobs.renderer.VindorRenderer
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.CompositeEntityModel
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.mob.MobEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

object MobRegistry {
    fun registerMobs(client: Boolean) {
        if (client) {
            registry.forEach {
                EntityRendererRegistry.INSTANCE.register(
                    it.entityType
                ) { dispatcher, _ ->
                    it.rendererFactory(dispatcher)
                }
            }
        } else {
            vindor.create(Identifier(BefriendMinecraft.NAMESPACE, "vindor"), 0.6f, 1.95f, 8)
            evione.create(Identifier(BefriendMinecraft.NAMESPACE, "evione"), 0.6f, 1.95f, 8)
        }
    }

    val vindor = MobStruct(::Vindor, ::VindorRenderer)
    val evione = MobStruct(::Evione, ::EvioneRenderer)
    private val registry = arrayListOf(vindor, evione)

}

class MobStruct<T : MobEntity, V : CompositeEntityModel<T>>(
    private val entityFactory: KFunction2<
            @ParameterName(name = "entityType") EntityType<T>,
            @ParameterName(name = "world") World,
            T>,
    val rendererFactory: KFunction1<
            @ParameterName(name = "entityRenderDispatcher") EntityRenderDispatcher?,
            MobEntityRenderer<T, V>>
) {
    var entityType: EntityType<T>? = null

    fun create(id: Identifier, width: Float, height: Float, trackingDistance: Int) {
        if (entityType != null) return
        val fabricEntity = FabricEntityTypeBuilder.create<T>(SpawnGroup.CREATURE, entityFactory)
        entityType = Registry.register(
            Registry.ENTITY_TYPE,
            id,
            fabricEntity.dimensions(EntityDimensions(width, height, true)).trackable(trackingDistance, 3).build()
        )
    }
}