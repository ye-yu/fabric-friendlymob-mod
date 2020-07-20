package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.mobs.entity.Evione
import fp.yeyu.monsterfriend.screens.widget.WColoredBar
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.LiteralText
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.stream.IntStream

class EvioneGUI(
    syncId: Int,
    player: PlayerInventory,
    handlerContext: ScreenHandlerContext,
    private val evione: Evione?
) : SyncedGuiDescription(
    Screens.evioneScreen, syncId, player,
    getBlockInventory(handlerContext, SIZE),
    getBlockPropertyDelegate(handlerContext, 0)
) {
    private val wProgressBar = WColoredBar
        .Builder(Evione.MAX_PROGRESS, 4, 17)
        .setDirection(WBar.Direction.UP)
        .gridOffset(3, 0)
        .build()

    private val dataTracker = DataTracker(player.player)

    init {
        val root = WGridPanel()
        setRootPanel(root)

        if (evione != null) {
            val inv = evione.getInventory()
            val synthesisStack = evione.mainHandStack
            val producedStack = inv.getStack(0)
            setBlockInventoryIfNotEmpty(0, synthesisStack)
            setBlockInventoryIfNotEmpty(2, producedStack)
        }

        IntStream.range(0, SIZE).forEach {
            val slot = WItemSlot.of(blockInventory, it)
            val label = createCenteredLabel(WIDGET_LABEL[it])
            if (it != 2) {
                root.add(slot, 1 + 2 * it, 1)
                root.add(label, 1 + 2 * it, 2)
            } else {
                root.add(slot, 1 + 2 * (it + 1), 1)
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

        initDataTracker()
        if (world.isClient) {
            wProgressBar.setFinalCallback {
                it.setProgress(getProgress().toInt())
            }
        }
    }

    private fun initDataTracker() {
        dataTracker.startTracking(PROGRESS, 0)
    }

    private fun setBlockInventoryIfNotEmpty(slot: Int, itemStack: ItemStack?) {
        if (itemStack == null || itemStack.isEmpty) return
        blockInventory.setStack(slot, itemStack)
    }

    @Environment(EnvType.CLIENT)
    private fun consume() {
        LOGGER.info("Calling from ${if (world.isClient) "client" else "server"} side. Current progress count: ${getProgress()}")
    }

    companion object {
        const val SIZE = 3
        val WIDGET_LABEL = arrayOf(
            "Target", "Fuel", "Output"
        )
        val LOGGER: Logger = LogManager.getLogger()
        val PROGRESS: TrackedData<Byte> = DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.BYTE)
    }

    override fun onSlotClick(slotNumber: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack {
        // can only pickup in slot 2
        if (slotNumber == 2 && !playerInventory.cursorStack.isEmpty) return ItemStack.EMPTY
        else if (slotNumber == 0) {
            return manageSlotZero(slotNumber, button, action, player)
        } else if (slotNumber == 1) {
            return manageSlotOne(slotNumber, button, action, player)
        } else if (slotNumber > 2 && action == SlotActionType.QUICK_MOVE) {
            if (blockInventory.getStack(0).isEmpty) {
                LOGGER.info("Must initialise progress")
                val selectedStack = slots[slotNumber].stack
                if (Evione.isEssence(selectedStack.item)) {
                    slots[1].stack = selectedStack
                    slots[slotNumber].stack = ItemStack.EMPTY
                    return ItemStack.EMPTY
                } else if (!selectedStack.isEmpty) {
                    val toTransfer = selectedStack.copy()
                    toTransfer.count = 1
                    selectedStack.decrement(1)
                    slots[0].stack = toTransfer
                    return selectedStack
                }
            } else if (blockInventory.getStack(1).isEmpty) {
                val selectedStack = slots[slotNumber].stack
                if (!Evione.isEssence(selectedStack.item)) {
                    return ItemStack.EMPTY
                }
            } else {
                val selectedStack = slots[slotNumber].stack
                if (blockInventory.getStack(0).item == selectedStack.item)
                // deny transfer any slot 1
                    return ItemStack.EMPTY
            }
            if (!blockInventory.getStack(0).isEmpty && !blockInventory.getStack(1).isEmpty && blockInventory.getStack(2).isEmpty) {
                // deny moving item to slot three
                return ItemStack.EMPTY
            } else {
                val selectedStack = slots[slotNumber].stack
                if (blockInventory.getStack(2).item == selectedStack.item) {
                    // deny combining item to slot three
                    return ItemStack.EMPTY
                }
            }
        }
        return super.onSlotClick(slotNumber, button, action, player)
    }

    private fun manageSlotOne(slotNumber: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack {
        if (!Evione.isEssence(playerInventory.cursorStack.item) && !playerInventory.cursorStack.isEmpty) {
            return ItemStack.EMPTY
        }
        return super.onSlotClick(slotNumber, button, action, player)
    }

    fun setProgress(p: Byte) {
        LOGGER.info("Calling from ${if (world.isClient) "client" else "server"} side. Setting progress to $p")
        this.dataTracker.set(PROGRESS, p)
    }

    private fun getProgress(): Byte {
        return this.dataTracker.get(PROGRESS)
    }

    private fun manageSlotZero(
        slotNumber: Int,
        button: Int,
        action: SlotActionType,
        player: PlayerEntity
    ): ItemStack {
        val outputStack = blockInventory.getStack(0)
        if (!playerInventory.cursorStack.isEmpty) {
            if (outputStack.isEmpty) {
                LOGGER.info("Must only put one item to slot")
                val cursorStack = playerInventory.cursorStack
                if (cursorStack.count > 1) {
                    val toTransfer = cursorStack.copy()
                    toTransfer.count = 1
                    cursorStack.decrement(1)
                    slots[0].stack = toTransfer
                    return cursorStack
                }
                evione?.setSynthesisItem(cursorStack)
                return super.onSlotClick(slotNumber, button, action, player)
            } else {
                LOGGER.info("Is swapping! Denying")
                return ItemStack.EMPTY
            }
        } else {
            LOGGER.info("Must clear progress")
            return super.onSlotClick(slotNumber, button, action, player)
        }
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
}
