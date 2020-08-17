package fp.yeyu.monsterfriend.mobs.renderer.model

import fp.yeyu.monsterfriend.mobs.entity.Wizard
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.VillagerResemblingModel
import net.minecraft.util.math.MathHelper

class WizardEntityModel(scale: Float) :
    VillagerResemblingModel<Wizard>(scale, 64, 128) {

    private var liftingNose = false

    init {
        head = ModelPart(this).setTextureSize(64, 128)
        head.setPivot(0.0f, 0.0f, 0.0f)
        head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale)
        field_17141 = ModelPart(this).setTextureSize(64, 128)
        field_17141.setPivot(-5.0f, -10.03125f, -5.0f)
        field_17141.setTextureOffset(0, 64).addCuboid(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f)
        head.addChild(field_17141)
        head.addChild(nose)
        val modelPart = ModelPart(this).setTextureSize(64, 128)
        modelPart.setPivot(1.75f, -4.0f, 2.0f)
        modelPart.setTextureOffset(0, 76).addCuboid(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f)
        modelPart.pitch = -0.05235988f
        modelPart.roll = 0.02617994f
        field_17141.addChild(modelPart)
        val modelPart2 = ModelPart(this).setTextureSize(64, 128)
        modelPart2.setPivot(1.75f, -4.0f, 2.0f)
        modelPart2.setTextureOffset(0, 87).addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f)
        modelPart2.pitch = -0.10471976f
        modelPart2.roll = 0.05235988f
        modelPart.addChild(modelPart2)
        val modelPart3 = ModelPart(this).setTextureSize(64, 128)
        modelPart3.setPivot(1.75f, -2.0f, 2.0f)
        modelPart3.setTextureOffset(0, 95).addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.25f)
        modelPart3.pitch = -0.20943952f
        modelPart3.roll = 0.10471976f
        modelPart2.addChild(modelPart3)
    }

    override fun setAngles(
        entity: Wizard,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
        nose.setPivot(0.0f, -2.0f, 0.0f)
        val f = 0.01f * (entity.entityId % 10).toFloat()
        nose.pitch = MathHelper.sin(entity.age.toFloat() * f) * 4.5f * 0.017453292f
        nose.yaw = 0.0f
        nose.roll = MathHelper.cos(entity.age.toFloat() * f) * 2.5f * 0.017453292f
        if (this.liftingNose) {
            nose.setPivot(0.0f, 1.0f, -1.5f)
            nose.pitch = -0.9f
        }
    }

    fun getNose(): ModelPart {
        return nose
    }

    fun setLiftingNose(bl: Boolean) {
        this.liftingNose = bl
    }
}