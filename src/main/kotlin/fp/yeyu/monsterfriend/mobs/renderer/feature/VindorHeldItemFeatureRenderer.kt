package fp.yeyu.monsterfriend.mobs.renderer.feature

import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.mobs.renderer.VindorRenderer
import fp.yeyu.monsterfriend.mobs.renderer.model.VindorEntityModel
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
import net.minecraft.client.util.math.MatrixStack

class VindorHeldItemFeatureRenderer(vindorRenderer: VindorRenderer) :
    HeldItemFeatureRenderer<Vindor, VindorEntityModel>(vindorRenderer) {

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        livingEntity: Vindor,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        if (livingEntity.isAttacking)
            super.render(
                matrixStack,
                vertexConsumerProvider,
                light,
                livingEntity,
                limbAngle,
                limbDistance,
                tickDelta,
                animationProgress,
                headYaw,
                headPitch
            )
    }

}
