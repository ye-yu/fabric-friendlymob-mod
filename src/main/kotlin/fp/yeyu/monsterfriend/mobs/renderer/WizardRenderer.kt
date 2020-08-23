package fp.yeyu.monsterfriend.mobs.renderer

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.mobs.entity.Wizard
import fp.yeyu.monsterfriend.mobs.renderer.feature.FoldedHandHeldItemFeatureRenderer
import fp.yeyu.monsterfriend.mobs.renderer.model.WizardEntityModel
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class WizardRenderer(entityRenderDispatcher: EntityRenderDispatcher?) :
    MobEntityRenderer<Wizard, WizardEntityModel>(entityRenderDispatcher, WizardEntityModel(0f), 0.5f) {

    init {
        addFeature(FoldedHandHeldItemFeatureRenderer(this))
    }
    private val texture = Identifier(BefriendMinecraft.NAMESPACE, "textures/entity/wizard.png")
    override fun getTexture(entity: Wizard?): Identifier = texture

    override fun scale(entity: Wizard, matrices: MatrixStack, amount: Float) {
        val g = 0.9375f
        matrices.scale(g, g, g)

    }

    override fun render(
        mobEntity: Wizard,
        f: Float,
        g: Float,
        matrixStack: MatrixStack?,
        vertexConsumerProvider: VertexConsumerProvider?,
        i: Int
    ) {
        model.setLiftingNose(!mobEntity.mainHandStack.isEmpty)
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }
}