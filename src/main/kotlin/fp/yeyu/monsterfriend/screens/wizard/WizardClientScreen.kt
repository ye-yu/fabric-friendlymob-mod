package fp.yeyu.monsterfriend.screens.wizard

import com.mojang.blaze3d.systems.RenderSystem
import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.children.LabelWidget
import io.github.yeyu.gui.renderer.widget.parents.InventoryPanel
import io.github.yeyu.gui.renderer.widget.parents.Panel
import io.github.yeyu.util.DrawerUtil
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Rectangle

class WizardClientScreen<T : ScreenRendererHandler>(
    handler: T,
    inventory: PlayerInventory,
    title: Text
) : ScreenRenderer<T>(handler, inventory, title, TEXTURE) {

    companion object {
        private val TEXTURE = Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/wizard/main.png")
        private val SCROLLBAR_TEXTURE: Identifier = Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/wizard/scrollbar.png")
    }

    init {
        backgroundWidth = 276
        playerInventoryTitleX = 107
    }

    override fun init() {
        super.init()

        createBlockInventorySlots()
        createPlayerInventorySlots()

        val panel = Panel(
            height = backgroundHeight,
            width = backgroundWidth,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            backgroundColor = 0,
            name = "full-panel"
        )

        panel.add(
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

        val scrollHeight = 140

        val scrollBar = JamcguiTwoStatesTexturedScrollBarWidget(
            relativeX = 44,
            relativeY = 18,
            width = 6,
            height = 27,
            scrollHeight = scrollHeight,
            texture = TextureDrawerHelper(SCROLLBAR_TEXTURE, 0, 0, 6, 27, 0, 0, 12, 27),
            inactiveTexture = TextureDrawerHelper(SCROLLBAR_TEXTURE, 6, 0, 6, 27, 0, 0, 12, 27),
            relativeBound = Rectangle(-89, 0, 95, scrollHeight),
            name = WizardPackets.SCROLLBAR
        ).apply { this.scrollablePredicate = { n: Int -> n > 2 } }

        panel.add(scrollBar)

        JamcguiRecipePanel(
            relativeX = -89,
            relativeY = 5,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "recipe-panel"
        ).apply {
            this@WizardClientScreen.addParent(this)
            this@WizardClientScreen.addListener(this)
        }

        addParent(panel)
        addListener(scrollBar)
    }

    private fun createPlayerInventorySlots() {
        val playerInventoryPanel = InventoryPanel(
            relativeX = 50,
            relativeY = 32,
            height = 64,
            width = backgroundWidth - 14,
            inventoryIndex = 9,
            numberOfSlots = 9 * 3,
            cols = 9,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "player-inv"
        )

        val hotbarPanel = InventoryPanel(
            relativeX = 50,
            relativeY = 68,
            height = 20,
            width = backgroundWidth - 14,
            inventoryIndex = 0,
            numberOfSlots = 9,
            cols = 9,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "hotbar"
        )

        addParent(playerInventoryPanel)
        addParent(hotbarPanel)
        addListener(playerInventoryPanel)
        addListener(hotbarPanel)
    }

    private fun createBlockInventorySlots() {
        for(i in 0..3) createBlockInventorySlot(i)
        val slot = InventoryPanel(
            relativeX = 162,
            relativeY = -36,
            height = 20,
            width = backgroundWidth - 14,
            inventoryIndex = playerInventory.size() + 4,
            numberOfSlots = 1,
            cols = 1,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "wizard-inv-4"
        )

        addParent(slot)
        addListener(slot)
    }

    private fun createBlockInventorySlot(index: Int) {
        val xOffset = 26
        val yOffset = 22
        val slot = InventoryPanel(
            relativeX = 78 + if (index % 2 == 0) 0 else xOffset,
            relativeY = -45 + if (index < 2) 0 else yOffset,
            height = 20,
            width = backgroundWidth - 14,
            inventoryIndex = playerInventory.size() + index,
            numberOfSlots = 1,
            cols = 1,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "wizard-inv-$index"
        )

        addParent(slot)
        addListener(slot)
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