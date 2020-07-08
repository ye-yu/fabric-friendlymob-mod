package fp.yeyu.mcvisualmod.screens

import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.SlotActionType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class VindorGUI(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext, val trader: Vindor?) :
    SyncedGuiDescription(
        Screens.VINDOR_SCREEN,
        syncId,
        playerInventory,
        getBlockInventory(context, SIZE),
        getBlockPropertyDelegate(context, SIZE)
    ) {
    init {
        val root = WGridPanel()
        setRootPanel(root)

        if (trader != null) {
            val inv = trader.getInventory(null, null, null)
            val invStack = inv.getStack(0)
            if (!isEmptyOrNull(invStack))
                blockInventory.setStack(0, invStack)
        }

        val vindorSlot = WItemSlot.of(blockInventory, 0)
        root.add(vindorSlot, 4, 1)

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        root.setSize(playerSlot.width, 80)
        root.validate(this)
    }

    companion object {
        val SIZE = 1
        val LOGGER: Logger = LogManager.getLogger()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        if (trader == null) return
        trader.currentCustomer = null

        val inv = trader.getInventory(null, null, null)
        val blockStack = blockInventory.getStack(0)
        if (!isEmptyOrNull(blockStack))
            inv.setStack(0, blockStack)
    }

    override fun onSlotClick(slotNumber: Int, button: Int, action: SlotActionType?, player: PlayerEntity?): ItemStack {
        LOGGER.info(String.format("%s clicked on slot %d", player?.entityName, slotNumber))
        return super.onSlotClick(slotNumber, button, action, player)
    }

    fun isEmptyOrNull(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack.isEmpty
    }

}