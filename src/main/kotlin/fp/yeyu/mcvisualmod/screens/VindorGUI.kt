package fp.yeyu.mcvisualmod.screens

import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import fp.yeyu.mcvisualmod.packets.PacketHandlers
import fp.yeyu.mcvisualmod.screens.widget.WTextField
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
        getBlockPropertyDelegate(context, SIZE)
    ) {

    private val msgField = WTextField()

    init {
        val root = WGridPanel()
        setRootPanel(root)

        if (vindor != null) {
            val inv = vindor.getInventory()
            val sendStack = inv.getStack(0)
            if (!sendStack.isEmpty) {
                blockInventory.setStack(0, sendStack)
                if (sendStack.getSubTag(Vindor.WONDER_MSG_TAG) != null) {
                    val msg = sendStack.getSubTag(Vindor.WONDER_MSG_TAG)!!.getString("msg")
                    msgField.text = msg
                }
            }

            val receiveStack = inv.getStack(1)
            if (!receiveStack.isEmpty) {
                blockInventory.setStack(1, receiveStack)
            }
        }

        root.add(
            WLabel("Send")
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER),
            5,
            2
        )
        val toSendSlot = WItemSlot.of(blockInventory, 0)
        root.add(toSendSlot, 5, 1)


        root.add(
            WLabel("Receive")
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER),
            7,
            2
        )
        val toReceiveSlot = WItemSlot.of(blockInventory, 1)
        root.add(toReceiveSlot, 7, 1)

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        msgField.isEditable = false
        msgField.maxLength = MAX_TEXT_LENGTH
        msgField.width = 4 * 18 - io.github.cottonmc.cotton.gui.widget.WTextField.OFFSET_X_TEXT * 2
        msgField.focusedBackgroundColor = -0x1666666
        msgField.enabledBackgroundColor = -0x1AAAAAA
        msgField.disabledBackgroundColor = -0x1000000
        msgField.focusedBorderColor = -0x1000000
        msgField.outfocusedBorderColor = -0x1000000
        msgField.caretColor = -0x1000000
        msgField.setEnabledColor(0x0)

        if (this.world.isClient) {
            msgField.onChangeListener = {
                val buf = PacketByteBuf(Unpooled.buffer())
                buf.writeString(it)
                PacketHandlers.VINDOR_SEND_TEXT.send(this.world, buf, null)
            }
            requestVindorText()
        }

        root.add(msgField, 0, 1, 4, 1)
        root.add(
            WLabel("Your Message")
                .setVerticalAlignment(VerticalAlignment.CENTER),
            0,
            2
        )

        root.setSize(playerSlot.width, 100)
        root.validate(this)
    }

    private fun requestVindorText() {
        LOGGER.info("Requesting vindor text from client side.")
        Magic.push(Magic.Key.OPEN_SCREEN, this)
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
        val inv = vindor.getInventory()
        val sendStack = blockInventory.getStack(0)
        val receiveStack = blockInventory.getStack(1)

        if (sendStack != null && !sendStack.isEmpty) {
            inv.setStack(0, sendStack)
        } else {
            inv.setStack(0, ItemStack.EMPTY)
        }

        if (receiveStack == null || receiveStack.isEmpty) {
            inv.setStack(1, ItemStack.EMPTY)
            vindor.dropStack(receiveStack)
            if (player != null) {
                vindor.flushMessage(player)
            }
        }
        vindor.finishTrading()
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