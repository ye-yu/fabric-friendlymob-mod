package fp.yeyu.monsterfriend.mobs.renderer

import fp.yeyu.monsterfriend.mobs.entity.Skelly
import fp.yeyu.monsterfriend.mobs.renderer.model.SkellyEntityModel
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.util.Identifier

class SkellyRenderer(entityRenderDispatcher: EntityRenderDispatcher?) :
    BipedEntityRenderer<Skelly, SkellyEntityModel>(entityRenderDispatcher, SkellyEntityModel(), 0.5f) {
    private val texture =
        Identifier("textures/entity/skeleton/skeleton.png")

    init {
        this.addFeature(
            ArmorFeatureRenderer(
                this,
                SkellyEntityModel(0.5f, true),
                SkellyEntityModel(1.0f, true)
            )
        )
    }

    override fun getTexture(mobEntity: Skelly?): Identifier = texture
}