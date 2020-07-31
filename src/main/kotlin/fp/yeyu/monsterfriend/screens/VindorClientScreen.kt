package fp.yeyu.monsterfriend.screens

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget.AnchorType
import io.github.yeyu.gui.renderer.widget.children.TexturedTextFieldWidget
import io.github.yeyu.gui.renderer.widget.parents.InventoryPanel
import io.github.yeyu.gui.renderer.widget.parents.Panel
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class VindorClientScreen<T: ScreenRendererHandler>(handler: T, player: PlayerInventory, title: Text) :
    ScreenRenderer<T>(handler, player, title) {
    companion object {
        private val TEXTURE: Identifier = Identifier("textures/gui/container/anvil.png")
        private const val TEXT_FIELD_HEIGHT = 16
        private const val TEXT_FIELD_WIDTH = 110
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

        val vindorInventory = InventoryPanel(
            relativeY = 2,
            height = 20,
            width = backgroundWidth - 14,
            inventoryIndex = playerInventory.size(),
            numberOfSlots = 2,
            cols = 2,
            horizontalAnchor = AnchorType.MIDDLE,
            verticalAnchor = AnchorType.MIDDLE,
            name = "vindor-inv"
        )

        val playerInventoryPanel = InventoryPanel(
            relativeY = 24,
            height = 20,
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
            relativeX = 63,
            relativeY = 24,
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
        this.addParent(vindorInventory)
        this.addParent(otherPanel)
        this.addListener(hotbarPanel)
        this.addListener(textField)

    }
    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
    }
}
