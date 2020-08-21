package fp.yeyu.monsterfriend.screens.evione

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.screens.gui.JamcguiProgressBarWidget
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.children.LabelWidget
import io.github.yeyu.gui.renderer.widget.children.SingleItemSlotWidget
import io.github.yeyu.gui.renderer.widget.parents.InventoryPanel
import io.github.yeyu.gui.renderer.widget.parents.Panel
import io.github.yeyu.util.DrawerUtil
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class EvioneClientScreen<T : ScreenRendererHandler>(
    handler: T,
    inventory: PlayerInventory,
    title: Text
) : ScreenRenderer<T>(handler, inventory, title, TEXTURE) {

    companion object {
        val TEXTURE = Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/evione.png")
    }

    override fun init() {
        super.init()


        val evioneFuelSlot = InventoryPanel(
            relativeX = -52,
            relativeY = -35,
            height = SingleItemSlotWidget.SQUARE_SIZE,
            width = SingleItemSlotWidget.SQUARE_SIZE,
            inventoryIndex = playerInventory.size() + 1,
            numberOfSlots = 1,
            cols = 1,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "evione-inv-2"
        )

        val evioneSynthesisSlot = InventoryPanel(
            relativeX = -23,
            relativeY = -35,
            height = SingleItemSlotWidget.SQUARE_SIZE,
            width = SingleItemSlotWidget.SQUARE_SIZE,
            inventoryIndex = playerInventory.size(),
            numberOfSlots = 1,
            cols = 1,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "evione-inv-1"
        )


        val evioneResultingSlot = InventoryPanel(
            relativeX = 42,
            relativeY = -35,
            height = SingleItemSlotWidget.SQUARE_SIZE,
            width = SingleItemSlotWidget.SQUARE_SIZE,
            inventoryIndex = playerInventory.size() + 2,
            numberOfSlots = 1,
            cols = 1,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            name = "evione-inv-3"
        )

        val playerInventoryPanel = InventoryPanel(
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

        val progressBar = JamcguiProgressBarWidget(
            relativeX = 80,
            relativeY = 45,
            width = 32,
            height = 4,
            name = EvionePacket.PROGRESS_NAME
        )

        val labellingPanel = Panel(
            height = backgroundHeight,
            width = backgroundWidth,
            horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
            verticalAnchor = ParentWidget.AnchorType.MIDDLE,
            backgroundColor = 0,
            name = "other-panel"
        )

        labellingPanel.add(progressBar)

        labellingPanel.add(
            LabelWidget(
                relativeX = backgroundWidth / 2,
                relativeY = 7,
                horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
                label = title,
                shadow = false,
                name = "title",
                color = DrawerUtil.constructColor(0x3a, 0x3b, 0x4a, 0xff)
            )
        )

        labellingPanel.add(
            LabelWidget(
                relativeX = 36,
                relativeY = 30,
                horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
                label = LiteralText("Fuel"),
                shadow = false,
                name = "fuel-label",
                color = DrawerUtil.constructColor(0x3a, 0x3b, 0x4a, 0xff)
            )
        )

        labellingPanel.add(
            LabelWidget(
                relativeX = 65,
                relativeY = 30,
                horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
                label = LiteralText("Target"),
                shadow = false,
                name = "target-label",
                color = DrawerUtil.constructColor(0x3a, 0x3b, 0x4a, 0xff)
            )
        )

        labellingPanel.add(
            LabelWidget(
                relativeX = 129,
                relativeY = 23,
                horizontalAnchor = ParentWidget.AnchorType.MIDDLE,
                label = LiteralText("Output"),
                shadow = false,
                name = "receive-label",
                color = DrawerUtil.constructColor(0x3a, 0x3b, 0x4a, 0xff)
            )
        )


        this.addParent(hotbarPanel)
        this.addParent(playerInventoryPanel)
        this.addParent(evioneFuelSlot)
        this.addParent(evioneSynthesisSlot)
        this.addParent(evioneResultingSlot)
        this.addParent(labellingPanel)

        this.addListener(hotbarPanel)
        this.addListener(playerInventoryPanel)
        this.addListener(evioneFuelSlot)
        this.addListener(evioneSynthesisSlot)
        this.addListener(evioneResultingSlot)
    }
}
