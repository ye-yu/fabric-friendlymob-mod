package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.packets.PacketHandlers
import fp.yeyu.monsterfriend.screens.widget.WTextField
import fp.yeyu.monsterfriend.statics.mutable.OpenedScreen
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import io.netty.buffer.Unpooled
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.SlotActionType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class VindorGUI(
    syncId: Int,
    playerInventory: PlayerInventory?,
    context: ScreenHandlerContext,
    val vindor: Vindor?
) :
    SyncedGuiDescription(
        Screens.VINDOR_SCREEN,
        syncId,
        playerInventory,
        getBlockInventory(context, SIZE),
        getBlockPropertyDelegate(context, 0)
    ) {

    private val msgField = WTextField()

    init {
        val root = WGridPanel()
        setRootPanel(root)

        if (vindor != null) {
            val inv = vindor.getInventory()
            val sendStack = inv.getStack(0)
            val receiveStack = inv.getStack(1)
            setBlockInventoryIfNotEmpty(0, sendStack)
            setBlockInventoryIfNotEmpty(1, receiveStack)
        }

        root.add(createCenteredLabel("Send"), 5, 2)
        val toSendSlot = WItemSlot.of(blockInventory, 0)
        root.add(toSendSlot, 5, 1)


        root.add(createCenteredLabel("Receive"), 7, 2)
        val toReceiveSlot = WItemSlot.of(blockInventory, 1)
        root.add(toReceiveSlot, 7, 1)

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        initMessageField()

        root.add(msgField, 0, 1, 4, 1)
        root.add(createCenteredLabel("Your Message"), 0, 2)

        root.setSize(playerSlot.width, 100)
        root.validate(this)
    }

    private fun initMessageField() {
        msgField.isEditable = false
        msgField.maxLength = MAX_TEXT_LENGTH
        msgField.width = 4 * 18 - io.github.cottonmc.cotton.gui.widget.WTextField.OFFSET_X_TEXT * 2
        msgField.focusedBackgroundColor = -0x1666666
        msgField.enabledBackgroundColor = -0x1AAAAAA
        msgField.disabledBackgroundColor = -0x1000000
        msgField.focusedBorderColor = -0x1000000
        msgField.outFocusedBorderColor = -0x1000000
        msgField.caretColor = -0x1000000
        msgField.outFocusedTextColor = 0xFFFFFFF
        msgField.setEnabledColor(0x0)

        if (this.world.isClient) {
            msgField.onChangeListener = {
                val buf = PacketByteBuf(Unpooled.buffer())
                buf.writeString(it)
                PacketHandlers.VINDOR_SEND_TEXT.send(this.world, buf, null)
            }
            requestVindorText()
        }
    }

    private fun createCenteredLabel(label: String): WLabel {
        return WLabel(label)
            .setVerticalAlignment(VerticalAlignment.CENTER)
            .setHorizontalAlignment(HorizontalAlignment.CENTER)
    }

    private fun setBlockInventoryIfNotEmpty(slotNumber: Int, itemStack: ItemStack) {
        if (itemStack.isEmpty) return
        blockInventory.setStack(slotNumber, itemStack)
    }

    private fun requestVindorText() {
        OpenedScreen.set(this)
        PacketHandlers.VINDOR_REQUEST_TEXT.send(this.world, PacketByteBuf(Unpooled.buffer()), null)
    }

    companion object {
        const val SIZE = 2
        const val MAX_TEXT_LENGTH = 50
        val LOGGER: Logger = LogManager.getLogger()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        if (vindor == null) return
        val sendStack = blockInventory.getStack(0)
        val receiveStack = blockInventory.getStack(1)
        vindor.finishTrading(sendStack, receiveStack)
    }

    override fun onSlotClick(slotNumber: Int, button: Int, action: SlotActionType?, player: PlayerEntity?): ItemStack {
        if (slotNumber == 1) {
            if (action == SlotActionType.PICKUP || action == SlotActionType.PICKUP_ALL) {
                val outputStack = blockInventory.getStack(1)
                if (outputStack == null || outputStack.isEmpty || outputStack.item == Items.AIR) {
                    return ItemStack.EMPTY
                }
            } else {
                return ItemStack.EMPTY
            }
        }
        return super.onSlotClick(slotNumber, button, action, player)
    }

    fun initText(msg: String) {
        msgField.text = msg
        msgField.isEditable = true
        vindor?.setSenderMessage(msg)
    }
}