package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.packets.PacketHandlers
import fp.yeyu.monsterfriend.packets.ScreenDescriptionPacketListener
import fp.yeyu.monsterfriend.screens.widget.WColoredBar
import fp.yeyu.monsterfriend.screens.widget.WListeningItemSlot
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.LiteralText
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.stream.IntStream

class EvioneScreenDescription(
    syncId: Int,
    player: PlayerInventory,
    handlerContext: ScreenHandlerContext,
    private val evione: Evione?
) : SlotConstrainedScreenDescription(
    Screens.EVIONE_SCREEN, syncId, player,
    getBlockInventory(handlerContext, SIZE),
    getBlockPropertyDelegate(handlerContext, 0)
), ScreenDescriptionPacketListener {
    private val s2cListeners = HashMap<String, (PacketByteBuf) -> Unit>()
    private val c2sListeners = HashMap<String, (PacketByteBuf) -> Unit>()
    private val wProgressBar = WColoredBar
        .Builder(Evione.MAX_PROGRESS, 4, 17)
        .setDirection(WBar.Direction.UP)
        .gridOffset(3, 0)
        .build()

    init {
        val root = WGridPanel()
        setRootPanel(root)
        getRootPanel()

        if (evione != null) {
            val inv = evione.getInventory()
            val synthesisStack = evione.mainHandStack
            val producedStack = inv.getStack(0)
            setBlockInventoryIfNotEmpty(0, synthesisStack)
            setBlockInventoryIfNotEmpty(2, producedStack)
        }

        IntStream.range(0, SIZE).forEach {
            val wItemSlot = WItemSlot.of(blockInventory, it)
            val label = createCenteredLabel(WIDGET_LABEL[it])
            if (it != 2) {
                root.add(wItemSlot, 1 + 2 * it, 1)
                root.add(label, 1 + 2 * it, 2)
            } else {
                root.add(wItemSlot, 1 + 2 * (it + 1), 1)
                root.add(label, 1 + 2 * (it + 1), 2)
            }
        }

        val wButton = WButton(LiteralText("->"))
        root.add(wButton, 5, 1)
        wButton.setOnClick(Runnable(::consume))
        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        root.add(wProgressBar, 8, 1)
        root.setSize(playerSlot.width, 100)
        root.validate(this)

        initSlotConstraints()
        initPacketListeners()

        if (world.isClient) {
            PacketHandlers.SCREEN_C2S.send(world, createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.INIT), null)
        }
    }

    private fun initPacketListeners() {
        c2sListeners[PacketIdentifiers.INIT] = {
            evione?.getSynthesisProgress()?.toInt()?.apply(::sendProgressToClient)
        }

        c2sListeners[PacketIdentifiers.CONSUME_ITEM] = {
            val itemStack = it.readItemStack()
            LOGGER.info("Received ${itemStack.item}: ${if (Evione.isEssence(itemStack.item)) "is an essence" else "is not an essence"}")

            val newProgress = world.random.nextInt(Evione.MAX_PROGRESS).apply(::sendProgressToClient)
            evione?.setProgress(newProgress.toByte())
        }

        s2cListeners[PacketIdentifiers.SET_PROGRESS] = {
            val progress = it.readInt()
            LOGGER.info("Received new progress of $progress")
            setProgress(progress)
        }
    }

    private fun initSlotConstraints() {
        setSlotCapacity(0, 1)
        setSlotPredicate(0) { !Evione.isEssence(it.item) } // don't put essence at first slot
        setSlotPredicate(1) { Evione.isEssence(it.item) } // only put essence at second slot
        setSlotPredicate(2) { it.isEmpty } // only pickup this slot

        setSlotListener(0) { finalStack: ItemStack ->
            if (evione != null) {
                val originalStack = evione.getInventory().getStack(0)
                if (finalStack == ItemStack.EMPTY) { // picked up
                    LOGGER.info("Picked up slot 0")
                    evione.clearSynthesisItem()
                }
                if (!ScreenHandler.canStacksCombine(originalStack, finalStack)) {
                    LOGGER.info("Swap / Placed slot 0")
                    evione.synthesisNewItem(finalStack)
                }

            }
        }

        setSlotListener(2) { originalStack: ItemStack, _: ItemStack ->
            if (evione != null) {
                val count = originalStack.count
                val outputStack = evione.getInventory().getStack(0)
                outputStack.decrement(count)
                evione.getInventory().setStack(0, outputStack)
            }
        }
    }

    private fun setBlockInventoryIfNotEmpty(slot: Int, itemStack: ItemStack?) {
        if (itemStack == null || itemStack.isEmpty) return
        blockInventory.setStack(slot, itemStack)
    }

    @Environment(EnvType.CLIENT)
    private fun consume() {
        check(world.isClient)
        val buffer = createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.CONSUME_ITEM)
        val randomItem = Registry.ITEM.getRandom(world.random)
        buffer.writeItemStack(ItemStack(randomItem))
        PacketHandlers.SCREEN_C2S.send(world, buffer, null)
        LOGGER.info("Sending request to consume $randomItem")
    }

    companion object {
        const val SIZE = 3
        val WIDGET_LABEL = arrayOf(
            "Target", "Fuel", "Output"
        )
        val LOGGER: Logger = LogManager.getLogger()
    }

    object PacketIdentifiers {
        const val CONSUME_ITEM = "consume-item"
        const val SET_PROGRESS = "set-progress"
        const val INIT = "init"
    }

    fun setProgress(p: Int) {
        wProgressBar.setProgress(p)
    }

    fun sendProgressToClient(p: Int) {
        val buf = createWrappedPacketBuffer(getSyncId(), PacketIdentifiers.SET_PROGRESS)
        buf.writeInt(p)
        PacketHandlers.SCREEN_S2C.send(world, buf, playerInventory.player)
    }

    private fun createCenteredLabel(label: String): WLabel {
        var wLabel = WLabel(label)
        wLabel = wLabel.setVerticalAlignment(VerticalAlignment.CENTER)
        wLabel = wLabel.setHorizontalAlignment(HorizontalAlignment.CENTER)
        return wLabel
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        if (evione == null) return
        evione.endInteraction()
    }

    override fun getSyncId(): Int {
        return syncId
    }

    override fun getS2CListeners(): HashMap<String, (PacketByteBuf) -> Unit> {
        return s2cListeners
    }

    override fun getC2SListeners(): HashMap<String, (PacketByteBuf) -> Unit> {
        return c2sListeners
    }

    override fun onClient2Server(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
        super.onClient2Server(packetContext, packetByteBuf)
    }

    override fun onServer2Client(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
        super.onServer2Client(packetContext, packetByteBuf)
    }

    fun setOutputStack(synthesisStack: ItemStack) {
        LOGGER.info("Setting display output stack to ${synthesisStack.item}x${synthesisStack.count}")
        blockInventory.setStack(2, synthesisStack)
    }
}
