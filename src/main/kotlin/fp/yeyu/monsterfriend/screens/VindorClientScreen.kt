package fp.yeyu.monsterfriend.screens

import com.mojang.blaze3d.systems.RenderSystem
import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.gui.renderer.widget.children.TexturedTextFieldWidget
import io.github.yeyu.gui.renderer.widget.parents.InventoryPanel
import io.github.yeyu.gui.renderer.widget.parents.Panel
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class VindorClientScreen<T : ScreenRendererHandler>(handler: T, player: PlayerInventory, title: Text) :
        ScreenRenderer<T>(handler, player, title) {
    companion object {
        private val TEXTURE: Identifier = Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/vindor.png")
        private const val TEXT_FIELD_HEIGHT = 16
        private const val TEXT_FIELD_WIDTH = 82
        private const val TEXTURE_PADDING = 4
        private val OUT_FOCUSED_BG: TextureDrawerHelper = TextureDrawerHelper(
                TEXTURE,
                0, 166 + TEXT_FIELD_HEIGHT,
                TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT,
                -TEXTURE_PADDING, -TEXTURE_PADDING
        )
        private val FOCUSED_BG: TextureDrawerHelper = TextureDrawerHelper(
                TEXTURE,
                0, 166,
                TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT,
                -TEXTURE_PADDING, -TEXTURE_PADDING
        )
    }

    override fun init() {
        super.init()

        val vindorSendSlot = InventoryPanel(
                relativeX = 36,
                relativeY = -47,
                height = 20,
                width = backgroundWidth - 14,
                inventoryIndex = playerInventory.size(),
                numberOfSlots = 1,
                cols = 1,
                horizontalAnchor = AnchorType.MIDDLE,
                verticalAnchor = AnchorType.MIDDLE,
                name = "vindor-inv-1"
        )

        val vindorReceivedSlot = InventoryPanel(
                relativeX = 120,
                relativeY = -37,
                height = 20,
                width = backgroundWidth - 14,
                inventoryIndex = playerInventory.size() + 1,
                numberOfSlots = 1,
                cols = 1,
                horizontalAnchor = AnchorType.MIDDLE,
                verticalAnchor = AnchorType.MIDDLE,
                name = "vindor-inv-2"
        )


        val playerInventoryPanel = InventoryPanel(
                relativeY = 32,
                height = 64,
                width = backgroundWidth - 14,
                inventoryIndex = 9,
                numberOfSlots = 9 * 3,
                cols = 9,
                horizontalAnchor = AnchorType.MIDDLE,
                verticalAnchor = AnchorType.MIDDLE,
                name = "player-inv"
        )

        val hotbarPanel = InventoryPanel(
                relativeY = 68,
                height = 20,
                width = backgroundWidth - 14,
                inventoryIndex = 0,
                numberOfSlots = 9,
                cols = 9,
                horizontalAnchor = AnchorType.MIDDLE,
                verticalAnchor = AnchorType.MIDDLE,
                name = "hotbar"
        )

        val textField = TexturedTextFieldWidget(
                relativeX = 14,
                relativeY = 58,
                width = TEXT_FIELD_WIDTH - 2 * TEXTURE_PADDING,
                height = TEXT_FIELD_HEIGHT,
                focusedTexture = FOCUSED_BG,
                outFocusedTexture = OUT_FOCUSED_BG,
                name = VindorPackets.VINDOR_TEXT_FIELD_NAME
        )

        val otherPanel = Panel(
                height = backgroundHeight,
                width = backgroundWidth,
                horizontalAnchor = AnchorType.MIDDLE,
                verticalAnchor = AnchorType.MIDDLE,
                backgroundColor = 0,
                name = "other-panel"
        )

        otherPanel.add(textField)

        this.addParent(hotbarPanel)
        this.addParent(playerInventoryPanel)
        this.addParent(vindorSendSlot)
        this.addParent(vindorReceivedSlot)
        this.addParent(otherPanel)

        this.addListener(hotbarPanel)
        this.addListener(textField)
        this.addListener(playerInventoryPanel)
        this.addListener(vindorSendSlot)
        this.addListener(vindorReceivedSlot)

    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        @Suppress("DEPRECATION")
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(TEXTURE)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)
    }
}
