package fp.yeyu.monsterfriend.mobs.renderer

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.mobs.renderer.feature.EvioneHeldItemFeatureRenderer
import fp.yeyu.monsterfriend.mobs.renderer.model.EvioneEntityModel
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.util.Identifier

class EvioneRenderer(
    entityRenderDispatcher: EntityRenderDispatcher?
) : MobEntityRenderer<Evione, EvioneEntityModel>(entityRenderDispatcher, EvioneEntityModel(0.0f, 0.0f, 64, 64), 0.5f) {

    init {
        addFeature(EvioneHeldItemFeatureRenderer(this))
    }

    companion object {
        val TEXTURE = Identifier(BefriendMinecraft.NAMESPACE, "textures/entity/evione.png")
    }

    override fun getTexture(entity: Evione?): Identifier {
        return TEXTURE
    }
}