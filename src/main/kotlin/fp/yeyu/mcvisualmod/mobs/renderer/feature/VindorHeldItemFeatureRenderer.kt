package fp.yeyu.mcvisualmod.mobs.renderer.feature

import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import fp.yeyu.mcvisualmod.mobs.renderer.VindorRenderer
import fp.yeyu.mcvisualmod.mobs.renderer.model.VindorEntityModel
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
import net.minecraft.client.util.math.MatrixStack

class VindorHeldItemFeatureRenderer(vindorRenderer: VindorRenderer) :
    HeldItemFeatureRenderer<Vindor, VindorEntityModel>(vindorRenderer) {

    override fun render(
        matrixStack: MatrixStack?,
        vertexConsumerProvider: VertexConsumerProvider?,
        i: Int,
        livingEntity: Vindor?,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        if (livingEntity != null) {
            if (livingEntity.isAttacking)
                super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l)
        }
    }

}
