package fp.yeyu.monsterfriend.mobs.renderer.model

import fp.yeyu.monsterfriend.mobs.entity.Skelly
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.CrossbowPosing
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper

class SkellyEntityModel(stretch: Float = 0f, isClothing: Boolean = false) :
    BipedEntityModel<Skelly>(stretch, 0f, 64, 64) {
    init {
        if (!isClothing) {
            rightArm = ModelPart(this, 40, 16)
            rightArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, stretch)
            rightArm.setPivot(-5.0f, 2.0f, 0.0f)
            leftArm = ModelPart(this, 40, 16)
            leftArm.mirror = true
            leftArm.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, stretch)
            leftArm.setPivot(5.0f, 2.0f, 0.0f)
            rightLeg = ModelPart(this, 0, 16)
            rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, stretch)
            rightLeg.setPivot(-2.0f, 12.0f, 0.0f)
            leftLeg = ModelPart(this, 0, 16)
            leftLeg.mirror = true
            leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, stretch)
            leftLeg.setPivot(2.0f, 12.0f, 0.0f)

            head.addChild(ModelPart(this, 0, 32).also {
                it.addCuboid(-5.0f, -6.0f, -5.0f, 10.0f, 1.0f, 10.0f, stretch)
            })

//            head.addChild(ModelPart(this, 24, 0).also {
//                it.addCuboid(-5.0f, -6.0f, 4.0f, 10.0f, 1.0f, 1.0f, stretch)
//            })
        }
    }

    override fun animateModel(mobEntity: Skelly, f: Float, g: Float, h: Float) {
        rightArmPose = ArmPose.EMPTY
        leftArmPose = ArmPose.EMPTY
        val itemStack: ItemStack = mobEntity.getStackInHand(Hand.MAIN_HAND)
        if (itemStack.item === Items.BOW && mobEntity.isAttacking) {
            if (mobEntity.mainArm == Arm.RIGHT) {
                rightArmPose = ArmPose.BOW_AND_ARROW
            } else {
                leftArmPose = ArmPose.BOW_AND_ARROW
            }
        }
        super.animateModel(mobEntity, f, g, h)
    }

    override fun setAngles(
        mobEntity: Skelly,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        super.setAngles(mobEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
        val itemStack: ItemStack = mobEntity.mainHandStack
        if (mobEntity.isAttacking && (itemStack.isEmpty || itemStack.item !== Items.BOW)) {
            val xMultiplier = MathHelper.sin(handSwingProgress * 3.1415927f)
            val yMultiplier =
                MathHelper.sin((1.0f - (1.0f - handSwingProgress) * (1.0f - handSwingProgress)) * 3.1415927f)
            rightArm.roll = 0.0f
            leftArm.roll = 0.0f
            rightArm.yaw = -(0.1f - xMultiplier * 0.6f)
            leftArm.yaw = 0.1f - xMultiplier * 0.6f
            rightArm.pitch = -1.5707964f
            leftArm.pitch = -1.5707964f
            rightArm.pitch -= xMultiplier * 1.2f - yMultiplier * 0.4f
            leftArm.pitch -= xMultiplier * 1.2f - yMultiplier * 0.4f
            CrossbowPosing.method_29350(this.rightArm, leftArm, animationProgress)
        }
    }

    override fun setArmAngle(arm: Arm, matrices: MatrixStack?) {
        val f = if (arm == Arm.RIGHT) 1.0f else -1.0f
        val modelPart = getArm(arm)
        modelPart.pivotX += f
        modelPart.rotate(matrices)
        modelPart.pivotX -= f
    }

    override fun getHeadParts(): MutableIterable<ModelPart> {
        return mutableListOf(head)
    }

}