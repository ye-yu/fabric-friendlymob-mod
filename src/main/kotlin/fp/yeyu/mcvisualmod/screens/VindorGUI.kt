package fp.yeyu.mcvisualmod.screens

import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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
            val inv = trader.getInventory()
            val sendStack = inv.getStack(0)
            if (!sendStack.isEmpty) {
                blockInventory.setStack(0, sendStack)
            }

            val receiveStack = inv.getStack(1)
            if (!receiveStack.isEmpty) {
                blockInventory.setStack(1, receiveStack)
            }
        }

        val toSendSlot = WItemSlot.of(blockInventory, 0)
        root.add(toSendSlot, 2, 1)

        val toReceiveSlot = WItemSlot.of(blockInventory, 1)
        root.add(toReceiveSlot, 6, 1)

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)

        root.setSize(playerSlot.width, 80)
        root.validate(this)
    }

    companion object {
        const val SIZE = 2
        val LOGGER: Logger = LogManager.getLogger()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        if (trader == null) return
        trader.currentCustomer = null

        val inv = trader.getInventory()
        val sendStack = blockInventory.getStack(0)
        val receiveStack = blockInventory.getStack(1)

        if (sendStack != null && !sendStack.isEmpty) {
            inv.setStack(0, sendStack)
        } else {
            inv.setStack(0, ItemStack.EMPTY)
        }

        if (receiveStack != null && !receiveStack.isEmpty) {
            inv.setStack(1, receiveStack)
        } else {
            inv.setStack(1, ItemStack.EMPTY)
        }
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
}