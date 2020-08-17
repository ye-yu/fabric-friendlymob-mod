package fp.yeyu.monsterfriend.mobs.renderer.feature

import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.mobs.renderer.model.EvioneEntityModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f

@Deprecated(
    "To be replaced with FoldedHandHeldItemFeatureRenderer",
    replaceWith = ReplaceWith("FoldedHandHeldItemFeatureRenderer")
)
class EvioneHeldItemFeatureRenderer(featureRendererContext: FeatureRendererContext<Evione, EvioneEntityModel>) :
    VillagerHeldItemFeatureRenderer<Evione, EvioneEntityModel>(featureRendererContext) {

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        evione: Evione,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        matrixStack.push()
        matrixStack.translate(0.0, 0.4, -0.4)
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f))
        val itemStack = evione.mainHandStack
        MinecraftClient.getInstance().heldItemRenderer.renderItem(
            evione,
            itemStack,
            ModelTransformation.Mode.GROUND,
            false,
            matrixStack,
            vertexConsumerProvider,
            i
        )
        matrixStack.pop()

    }
}