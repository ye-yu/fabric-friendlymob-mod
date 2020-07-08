package fp.yeyu.mcvisualmod.mobs.renderer

import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import fp.yeyu.mcvisualmod.mobs.renderer.feature.VindorHeldItemFeatureRenderer
import fp.yeyu.mcvisualmod.mobs.renderer.model.VindorEntityModel
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
    }

    override fun getTexture(entity: Vindor?): Identifier {
        return TEXTURE
    }

}