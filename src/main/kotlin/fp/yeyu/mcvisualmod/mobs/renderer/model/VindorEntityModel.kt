package fp.yeyu.mcvisualmod.mobs.renderer.model

import com.google.common.collect.ImmutableList
import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.model.CompositeEntityModel
import net.minecraft.client.render.entity.model.CrossbowPosing
import net.minecraft.client.render.entity.model.ModelWithArms
import net.minecraft.client.render.entity.model.ModelWithHead
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.util.function.Function


class VindorEntityModel(
    scale: Float,
    pivotY: Float,
    textureWidth: Int,
    textureHeight: Int
) : CompositeEntityModel<Vindor>(),
    ModelWithArms, ModelWithHead {
    private val head: ModelPart = ModelPart(this).setTextureSize(textureWidth, textureHeight)
    private val hat: ModelPart
    private val torso: ModelPart
    private val arms: ModelPart
    private val rightLeg: ModelPart
    private val leftLeg: ModelPart
    private val rightAttackingArm: ModelPart
    private val leftAttackingArm: ModelPart

    init {
        head.setPivot(0.0f, 0.0f + pivotY, 0.0f)
        head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale)
        hat = ModelPart(this, 32, 0).setTextureSize(textureWidth, textureHeight)
        hat.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, scale + 0.45f)
        head.addChild(hat)
        hat.visible = false
        val modelPart = ModelPart(this).setTextureSize(textureWidth, textureHeight)
        modelPart.setPivot(0.0f, pivotY - 2.0f, 0.0f)
        modelPart.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, scale)
        head.addChild(modelPart)
        torso = ModelPart(this).setTextureSize(textureWidth, textureHeight)
        torso.setPivot(0.0f, 0.0f + pivotY, 0.0f)
        torso.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, scale)
        torso.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, scale + 0.5f)
        arms = ModelPart(this).setTextureSize(textureWidth, textureHeight)
        arms.setPivot(0.0f, 0.0f + pivotY + 2.0f, 0.0f)
        arms.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, scale)
        val modelPart2 = ModelPart(this, 44, 22).setTextureSize(textureWidth, textureHeight)
        modelPart2.mirror = true
        modelPart2.addCuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, scale)
        arms.addChild(modelPart2)
        arms.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, scale)
        rightLeg = ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight)
        rightLeg.setPivot(-2.0f, 12.0f + pivotY, 0.0f)
        rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale)
        leftLeg = ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight)
        leftLeg.mirror = true
        leftLeg.setPivot(2.0f, 12.0f + pivotY, 0.0f)
        leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale)
        rightAttackingArm = ModelPart(this, 40, 46).setTextureSize(textureWidth, textureHeight)
        rightAttackingArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale)
        rightAttackingArm.setPivot(-5.0f, 2.0f + pivotY, 0.0f)
        leftAttackingArm = ModelPart(this, 40, 46).setTextureSize(textureWidth, textureHeight)
        leftAttackingArm.mirror = true
        leftAttackingArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale)
        leftAttackingArm.setPivot(5.0f, 2.0f + pivotY, 0.0f)
    }


    override fun getParts(): Iterable<ModelPart?>? {
        return ImmutableList.of(
            head,
            torso,
            rightLeg,
            leftLeg,
            arms,
            rightAttackingArm,
            leftAttackingArm
        )
    }

    override fun setAngles(
        vindor: Vindor,
        f: Float,
        g: Float,
        h: Float,
        i: Float,
        j: Float
    ) {
        head.yaw = i * 0.017453292f
        head.pitch = j * 0.017453292f
        arms.pivotY = 3.0f
        arms.pivotZ = -1.0f
        arms.pitch = -0.75f
        if (riding) {
            rightAttackingArm.pitch = -0.62831855f
            rightAttackingArm.yaw = 0.0f
            rightAttackingArm.roll = 0.0f
            leftAttackingArm.pitch = -0.62831855f
            leftAttackingArm.yaw = 0.0f
            leftAttackingArm.roll = 0.0f
            rightLeg.pitch = -1.4137167f
            rightLeg.yaw = 0.31415927f
            rightLeg.roll = 0.07853982f
            leftLeg.pitch = -1.4137167f
            leftLeg.yaw = -0.31415927f
            leftLeg.roll = -0.07853982f
        } else {
            rightAttackingArm.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 2.0f * g * 0.5f
            rightAttackingArm.yaw = 0.0f
            rightAttackingArm.roll = 0.0f
            leftAttackingArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f
            leftAttackingArm.yaw = 0.0f
            leftAttackingArm.roll = 0.0f
            rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f
            rightLeg.yaw = 0.0f
            rightLeg.roll = 0.0f
            leftLeg.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 1.4f * g * 0.5f
            leftLeg.yaw = 0.0f
            leftLeg.roll = 0.0f
        }
        val state = vindor.getState()
        if (state == Vindor.State.ATTACKING) {
            CrossbowPosing.method_29351(
                rightAttackingArm,
                leftAttackingArm,
                vindor,
                handSwingProgress,
                h
            )
        }
        val bl = state == Vindor.State.CROSSED
        arms.visible = bl
        leftAttackingArm.visible = !bl
        rightAttackingArm.visible = !bl
    }

    private fun method_2813(arm: Arm): ModelPart? {
        return if (arm == Arm.LEFT) leftAttackingArm else rightAttackingArm
    }

    fun method_2812(): ModelPart? {
        return hat
    }

    override fun getHead(): ModelPart? {
        return head
    }

    override fun setArmAngle(arm: Arm, matrices: MatrixStack?) {
        method_2813(arm)!!.rotate(matrices)
    }
}