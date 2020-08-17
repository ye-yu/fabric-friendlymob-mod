package fp.yeyu.monsterfriend.mobs.renderer.feature

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity

class FoldedHandHeldItemFeatureRenderer<E : LivingEntity, M : EntityModel<E>>(featureRendererContext: FeatureRendererContext<E, M>?) :
    FeatureRenderer<E, M>(featureRendererContext) {

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        livingEntity: E,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        // return if nothing to render anyway
        if (livingEntity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty && livingEntity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty) return

        matrixStack.push()
        matrixStack.translate(0.0, 0.4, -0.4)
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f))
        val mainItemStack = livingEntity.mainHandStack
        val offHandItemStack = livingEntity.offHandStack

        if (!mainItemStack.isEmpty)
            MinecraftClient.getInstance().heldItemRenderer.renderItem(
                livingEntity,
                mainItemStack,
                ModelTransformation.Mode.GROUND,
                false,
                matrixStack,
                vertexConsumerProvider,
                light
            )

        if (!offHandItemStack.isEmpty)
            MinecraftClient.getInstance().heldItemRenderer.renderItem(
                livingEntity,
                offHandItemStack,
                ModelTransformation.Mode.GROUND,
                false,
                matrixStack,
                vertexConsumerProvider,
                light
            )

        matrixStack.pop()

    }
}