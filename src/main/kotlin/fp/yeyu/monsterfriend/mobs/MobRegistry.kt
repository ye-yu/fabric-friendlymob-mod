package fp.yeyu.monsterfriend.mobs

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.mobs.egg.EvioneEgg
import fp.yeyu.monsterfriend.mobs.egg.VindorEgg
import fp.yeyu.monsterfriend.mobs.egg.WizardEgg
import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.mobs.entity.Wizard
import fp.yeyu.monsterfriend.mobs.renderer.EvioneRenderer
import fp.yeyu.monsterfriend.mobs.renderer.VindorRenderer
import fp.yeyu.monsterfriend.mobs.renderer.WizardRenderer
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.CompositeEntityModel
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.SpawnEggItem
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction4

object MobRegistry {
    val vindor = MobStruct(::Vindor, ::VindorRenderer, EggAttr(::VindorEgg, 3407872, 12369084), ModelAttr(), "vindor")
    val evione =
        MobStruct(::Evione, ::EvioneRenderer, EggAttr(::EvioneEgg, 0xFFF5500, 0xF006634), ModelAttr(), "evione")
    val wizard =
        MobStruct(::Wizard, ::WizardRenderer, EggAttr(::WizardEgg, 0xF0055FF, 0xF006634), ModelAttr(), "wizard")
    private val registry = listOf(vindor, evione, wizard)

    fun registerMobs() {
        registry.forEach { it.registerMob() }
    }

    fun registerMobRenderers() {
        registry.forEach { it.registerMobRenderer() }
    }

    fun registerEggs() {
        registry.forEach { it.registerEgg() }
    }

    class MobStruct<T : MobEntity, V : CompositeEntityModel<T>, E : SpawnEggItem>(
        private val entityFactory: KFunction2<
                @ParameterName(name = "entityType") EntityType<T>,
                @ParameterName(name = "world") World,
                T>,
        val rendererFactory: KFunction1<
                @ParameterName(name = "entityRenderDispatcher") EntityRenderDispatcher?,
                MobEntityRenderer<T, V>>,
        private val eggAttr: EggAttr<E>,
        private val modelAttr: ModelAttr,
        name: String
    ) {
        val entityType: EntityType<T> by lazy { entityTypeInit() }
        val egg: E by lazy { eggInit() }
        private val id = Identifier(BefriendMinecraft.NAMESPACE, name)
        private val eggId = Identifier(BefriendMinecraft.NAMESPACE, name + "_egg")

        private fun entityTypeInit(): EntityType<T> {
            val fabricEntity = FabricEntityTypeBuilder.create<T>(SpawnGroup.CREATURE, entityFactory)
            return Registry.register(
                Registry.ENTITY_TYPE,
                id,
                fabricEntity.dimensions(EntityDimensions(modelAttr.width, modelAttr.height, true))
                    .trackable(modelAttr.trackingDistance, 3).build()
            )
        }

        private fun eggInit(): E {
            return eggAttr.registerEgg(eggId, entityType)
        }

        fun registerMobRenderer() {
            EntityRendererRegistry.INSTANCE.register(
                entityType
            ) { dispatcher, _ ->
                rendererFactory(dispatcher)
            }
        }

        fun registerEgg() {
            Logger.info("Triggered entity egg registry for " + egg.translationKey)
        }

        fun registerMob() {
            Logger.info("Triggered entity registry for " + entityType.translationKey)
        }
    }

    class EggAttr<T>(
        val eggFactory: KFunction4<@ParameterName(name = "type") EntityType<*>, @ParameterName(
            name = "primaryColor"
        ) Int, @ParameterName(name = "secondaryColor") Int, @ParameterName(name = "settings") Item.Settings, T>,
        private val primaryColor: Int,
        private val secondaryColor: Int
    ) where T : SpawnEggItem {
        fun registerEgg(id: Identifier, entity: EntityType<*>): T {
            return Registry.register(
                Registry.ITEM,
                id,
                eggFactory(
                    entity,
                    primaryColor,
                    secondaryColor,
                    Item.Settings().maxCount(64).group(ItemGroup.MISC)
                )
            )
        }
    }

    data class ModelAttr(val width: Float = 0.55f, val height: Float = 1.85f, val trackingDistance: Int = 8)
}
