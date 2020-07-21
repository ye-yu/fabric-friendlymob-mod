package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.mobs.entity.Vindor
import fp.yeyu.monsterfriend.packets.PacketHandlers
import fp.yeyu.monsterfriend.packets.ScreenDescriptionPacketListener
import fp.yeyu.monsterfriend.screens.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class VindorScreenDescription(
    syncId: Int,
    playerInventory: PlayerInventory?,
    context: ScreenHandlerContext,
    val vindor: Vindor?
) :
    SlotConstrainedScreenDescription(
        Screens.VINDOR_SCREEN,
        syncId,
        playerInventory,
        getBlockInventory(context, SIZE),
        getBlockPropertyDelegate(context, 0)
    ), ScreenDescriptionPacketListener {

    private val msgField = WTextField()

    private val s2cListener = HashMap<String, (PacketByteBuf) -> Unit>()
    private val c2sListener = HashMap<String, (PacketByteBuf) -> Unit>()

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

        root.add(createCenteredLabel("Send", vertically = true, horizontally = true), 5, 2)
        val toSendSlot = WItemSlot.of(blockInventory, 0)
        root.add(toSendSlot, 5, 1)


        root.add(createCenteredLabel("Receive", vertically = true, horizontally = true), 7, 2)
        val toReceiveSlot = WItemSlot.of(blockInventory, 1)
        root.add(toReceiveSlot, 7, 1)

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        initMessageField()

        root.add(msgField, 0, 1, 4, 1)
        root.add(createCenteredLabel("Your Message", vertically = true, horizontally = false), 0, 2)

        root.setSize(playerSlot.width, 100)
        root.validate(this)

        setSlotPredicate(1) { it.isEmpty }
        initPacketListeners()

        if (world.isClient) {
            PacketHandlers.SCREEN_C2S.send(world, createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.INIT), null)
        }
    }

    private fun initPacketListeners() {
        c2sListener[PacketIdentifiers.INIT] = {
            vindor?.getSenderMessage()?.apply {
                val buf = createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.SERVER_VINDOR_TEXT)
                buf.writeString(this)
                PacketHandlers.SCREEN_S2C.send(world, buf, playerInventory!!.player)
                LOGGER.info("Sending init text: $this")
            }
        }

        c2sListener[PacketIdentifiers.CLIENT_FIELD_TEXT] = {
            val text = it.readString()
            vindor?.setSenderMessage(text)
        }

        s2cListener[PacketIdentifiers.SERVER_VINDOR_TEXT] = {
            val text = it.readString()
            initText(text)
            LOGGER.info("Got the text: $text")
        }
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
                val buf = createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.CLIENT_FIELD_TEXT)
                buf.writeString(it)
                PacketHandlers.SCREEN_C2S.send(world, buf, null)
            }
        }
    }

    object PacketIdentifiers {
        const val INIT = "init"
        const val CLIENT_FIELD_TEXT = "client-field-text"
        const val SERVER_VINDOR_TEXT = "server-vindor-text"
    }

    private fun createCenteredLabel(label: String, vertically: Boolean, horizontally: Boolean): WLabel {
        var wLabel = WLabel(label)
        if (vertically) {
            wLabel = wLabel.setVerticalAlignment(VerticalAlignment.CENTER)
        }
        if (horizontally) {
            wLabel = wLabel.setHorizontalAlignment(HorizontalAlignment.CENTER)

        }
        return wLabel
    }

    private fun setBlockInventoryIfNotEmpty(slotNumber: Int, itemStack: ItemStack) {
        if (itemStack.isEmpty) return
        blockInventory.setStack(slotNumber, itemStack)
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

    fun initText(msg: String) {
        msgField.text = msg
        msgField.isEditable = true
    }

    override fun getSyncId(): Int {
        return syncId
    }

    override fun getS2CListeners(): HashMap<String, (PacketByteBuf) -> Unit> {
        return s2cListener
    }

    override fun getC2SListeners(): HashMap<String, (PacketByteBuf) -> Unit> {
        return c2sListener
    }
}