package fp.yeyu.monsterfriend.screens.wizard

import com.mojang.blaze3d.systems.RenderSystem
import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.children.LabelWidget
import io.github.yeyu.gui.renderer.widget.parents.Panel
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class WizardClientScreen<T : ScreenRendererHandler>(
    handler: T,
    inventory: PlayerInventory,
    title: Text
) : ScreenRenderer<T>(handler, inventory, title, TEXTURE) {

    companion object {
        private val TEXTURE = Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/wizard/main.png")
    }

    init {
        backgroundWidth = 276
        playerInventoryTitleX = 107
    }

    override fun init() {
        super.init()
        val otherPanel = Panel(
            height = backgroundHeight,
            width = backgroundWidth,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            backgroundColor = 0,
            name = "other-panel"
        )

        otherPanel.add(
            LabelWidget(
                relativeX = 101 + (backgroundWidth - 101) / 2,
                relativeY = 7,
                horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
                label = title,
                shadow = false,
                name = "title",
                color = DrawerUtil.constructColor(0x3a, 0x3b, 0x4a, 0xff)
            )
        )

        addParent(otherPanel)
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(TEXTURE)
        val drawX = (width - backgroundWidth) / 2
        val drawY = (height - backgroundHeight) / 2
        DrawableHelper.drawTexture(
            matrices,
            drawX,
            drawY,
            zOffset,
            0.0f,
            0.0f,
            backgroundWidth,
            backgroundHeight,
            256,
            512
        )

    }
}