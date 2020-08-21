package fp.yeyu.monsterfriend.mobs.renderer

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.mobs.renderer.feature.VindorHeldItemFeatureRenderer
import fp.yeyu.monsterfriend.mobs.renderer.model.VindorEntityModel
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer
import net.minecraft.util.Identifier

class VindorRenderer(entityRenderDispatcher: EntityRenderDispatcher?) :
    MobEntityRenderer<Vindor, VindorEntityModel>(entityRenderDispatcher, VindorEntityModel(0.0f, 0.0f, 64, 64), 0.5f) {
    init {
        addFeature(HeadFeatureRenderer<Vindor, VindorEntityModel>(this))
        addFeature(VindorHeldItemFeatureRenderer(this))
    }

    companion object {
        @JvmStatic
        val TEXTURE =
            Identifier("textures/entity/illager/vindicator.png")

        val READY = Identifier(BefriendMinecraft.NAMESPACE, "textures/entity/vindor/vindor-ready.png")
        val WAITING_1 = Identifier(BefriendMinecraft.NAMESPACE, "textures/entity/vindor/vindor-waiting.png")
        val WAITING_2 = Identifier(BefriendMinecraft.NAMESPACE, "textures/entity/vindor/vindor-waiting-2.png")
    }

    override fun getTexture(entity: Vindor?): Identifier {
        return when {
            entity == null -> TEXTURE
            entity.wonderState == Vindor.WonderState.NEUTRAL -> WAITING_1
            entity.wonderState == Vindor.WonderState.READY -> WAITING_2
            else -> READY
        }
    }

}