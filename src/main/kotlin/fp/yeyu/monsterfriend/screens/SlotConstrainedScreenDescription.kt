package fp.yeyu.monsterfriend.screens

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.ValidatedSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

open class SlotConstrainedScreenDescription(
    type: ScreenHandlerType<*>?,
    syncId: Int,
    playerInventory: PlayerInventory?,
    blockInventory: Inventory?,
    propertyDelegate: PropertyDelegate?
) : SyncedGuiDescription(type, syncId, playerInventory, blockInventory, propertyDelegate) {

    companion object {
        private val ALWAYS_TRUE: (ItemStack) -> Boolean = { true }
    }

    private val slotPredicates = HashMap<Int, (ItemStack) -> Boolean>()
    private val slotCapacity = HashMap<Int, Int>()

    fun setSlotPredicate(index: Int, predicate: (ItemStack) -> Boolean) {
        slotPredicates[index] = predicate
    }


    fun setSlotCapacity(index: Int, capacity: Int) {
        require(!(capacity > 64 || capacity < 1)) {
            String.format(
                "Capacity at slot %d must be 1 <= x 64 <=",
                index
            )
        }
        slotCapacity[index] = capacity
    }

    private fun insertIntoExisting(
        toInsert: ItemStack,
        slot: Slot,
        player: PlayerEntity
    ): Boolean {
        val curSlotStack = slot.stack
        if (!slotPredicates.getOrDefault(slot.id, ALWAYS_TRUE)(toInsert)) return false
        val slotCapacity = slotCapacity.getOrDefault(slot.id, toInsert.maxCount)
        val maxCount = Math.min(toInsert.maxCount, slotCapacity)
        if (!curSlotStack.isEmpty && ScreenHandler.canStacksCombine(toInsert, curSlotStack) && slot.canTakeItems(
                player
            )
        ) {
            val combinedAmount = curSlotStack.count + toInsert.count
            if (combinedAmount <= maxCount) {
                toInsert.count = 0
                curSlotStack.count = combinedAmount
                slot.markDirty()
                return true
            } else if (curSlotStack.count < maxCount) {
                toInsert.decrement(maxCount - curSlotStack.count)
                curSlotStack.count = maxCount
                slot.markDirty()
                return true
            }
        }
        return false
    }

    private fun insertIntoEmpty(toInsert: ItemStack, slot: Slot): Boolean {
        val curSlotStack = slot.stack
        if (!slotPredicates.getOrDefault(slot.id, ALWAYS_TRUE)(toInsert)) return false
        val slotCapacity = slotCapacity.getOrDefault(slot.id, toInsert.maxCount)
        if (curSlotStack.isEmpty && slot.canInsert(toInsert)) {
            if (toInsert.count > slotCapacity) {
                slot.stack = toInsert.split(slotCapacity)
            } else {
                slot.stack = toInsert.split(toInsert.count)
            }
            slot.markDirty()
            return true
        }
        return false
    }

    override fun onSlotClick(slotNumber: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
        if (slotNumber >= slots.size || slotNumber < 0) return ItemStack.EMPTY
        val slot = slots[slotNumber]
        if (action == SlotActionType.QUICK_MOVE) {
            if (slot == null || !slot.canTakeItems(player)) {
                return ItemStack.EMPTY
            }
            var remaining = ItemStack.EMPTY
            if (slot.hasStack()) {
                val toTransfer = slot.stack
                remaining = toTransfer.copy()
                //if (slot.inventory==blockInventory) {
                if (blockInventory != null) {
                    if (slot.inventory === blockInventory) {
                        //Try to transfer the item from the block into the player's inventory
                        if (!this.insertItem(toTransfer, playerInventory, true, player)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.insertItem(
                            toTransfer,
                            blockInventory,
                            false,
                            player
                        )
                    ) { //Try to transfer the item from the player to the block
                        return ItemStack.EMPTY
                    }
                } else {
                    //There's no block, just swap between the player's storage and their hotbar
                    if (!swapHotBar(toTransfer, slotNumber, playerInventory, player)) {
                        return ItemStack.EMPTY
                    }
                }
                if (toTransfer.isEmpty) {
                    slot.stack = ItemStack.EMPTY
                } else {
                    slot.markDirty()
                }
            }
            return remaining
        } else {
            var currentStack = slot!!.stack
            val targetStack = playerInventory.cursorStack
            if (targetStack.isEmpty) return super.onSlotClick(
                slotNumber,
                button,
                action,
                player
            ) // is picking up stack
            if (currentStack.isEmpty) {
                if (!insertIntoEmpty(targetStack, slot)) return ItemStack.EMPTY
            } else {
                if (!ScreenHandler.canStacksCombine(currentStack, targetStack)) {
                    // is swapping stack that does not have capacity constraint
                    // TODO: Maybe do it in `insertIntoExisting` instead?
                    if (!slotCapacity.containsKey(slotNumber)) {
                        // check slot constrain on target stack
                        return if (!slotPredicates.getOrDefault(slotNumber, ALWAYS_TRUE)(targetStack)) ItemStack.EMPTY
                        else super.onSlotClick(slotNumber, button, action, player)
                    }

                    // is swapping stack that does not resulted in overflowing capacity
                    // TODO: Maybe do it in `insertIntoExisting` instead?
                    if (targetStack.count <= slotCapacity[slotNumber]!!) {
                        // check slot constrain on target stack
                        return if (!slotPredicates.getOrDefault(slotNumber, ALWAYS_TRUE)(targetStack)) ItemStack.EMPTY
                        else super.onSlotClick(slotNumber, button, action, player)
                    }

                    // must drop stack to accommodate overflows
                    player.dropStack(currentStack)
                    currentStack = ItemStack.EMPTY
                    slot.stack = currentStack
                    if (!insertIntoEmpty(targetStack, slot)) return ItemStack.EMPTY
                } else {
                    if (!insertIntoExisting(targetStack, slot, player)) return ItemStack.EMPTY
                }
            }
            return targetStack
        }
    }

    private fun insertItem(
        toInsert: ItemStack,
        inventory: Inventory,
        walkBackwards: Boolean,
        player: PlayerEntity
    ): Boolean {
        //Make a unified list of slots *only from this inventory*
        val inventorySlots =
            ArrayList<Slot>()
        for (slot in slots) {
            if (slot.inventory === inventory) inventorySlots.add(slot)
        }
        if (inventorySlots.isEmpty()) return false

        //Try to insert it on top of existing stacks
        var inserted = false
        if (walkBackwards) {
            for (i in inventorySlots.indices.reversed()) {
                val curSlot = inventorySlots[i]
                if (insertIntoExisting(toInsert, curSlot, player)) inserted = true
                if (toInsert.isEmpty) break
            }
        } else {
            for (i in inventorySlots.indices) {
                val curSlot = inventorySlots[i]
                if (insertIntoExisting(toInsert, curSlot, player)) inserted = true
                if (toInsert.isEmpty) break
            }
        }

        //If we still have any, shove them into empty slots
        if (!toInsert.isEmpty) {
            if (walkBackwards) {
                for (i in inventorySlots.indices.reversed()) {
                    val curSlot = inventorySlots[i]
                    if (insertIntoEmpty(toInsert, curSlot)) inserted = true
                    if (toInsert.isEmpty) break
                }
            } else {
                for (i in inventorySlots.indices) {
                    val curSlot = inventorySlots[i]
                    if (insertIntoEmpty(toInsert, curSlot)) inserted = true
                    if (toInsert.isEmpty) break
                }
            }
        }
        return inserted
    }

    private fun swapHotBar(
        toInsert: ItemStack,
        slotNumber: Int,
        inventory: Inventory,
        player: PlayerEntity
    ): Boolean {
        //Feel out the slots to see what's storage versus hotbar
        val storageSlots =
            ArrayList<Slot>()
        val hotbarSlots =
            ArrayList<Slot>()
        var swapToStorage = true
        var inserted = false
        for (slot in slots) {
            if (slot.inventory === inventory && slot is ValidatedSlot) {
                val index = slot.inventoryIndex
                if (PlayerInventory.isValidHotbarIndex(index)) {
                    hotbarSlots.add(slot)
                } else {
                    storageSlots.add(slot)
                    if (index == slotNumber) swapToStorage = false
                }
            }
        }
        if (storageSlots.isEmpty() || hotbarSlots.isEmpty()) return false
        if (swapToStorage) {
            //swap from hotbar to storage
            for (i in storageSlots.indices) {
                val curSlot = storageSlots[i]
                if (insertIntoExisting(toInsert, curSlot, player)) inserted = true
                if (toInsert.isEmpty) break
            }
            if (!toInsert.isEmpty) {
                for (i in storageSlots.indices) {
                    val curSlot = storageSlots[i]
                    if (insertIntoEmpty(toInsert, curSlot)) inserted = true
                    if (toInsert.isEmpty) break
                }
            }
        } else {
            //swap from storage to hotbar
            for (i in hotbarSlots.indices) {
                val curSlot = hotbarSlots[i]
                if (insertIntoExisting(toInsert, curSlot, player)) inserted = true
                if (toInsert.isEmpty) break
            }
            if (!toInsert.isEmpty) {
                for (i in hotbarSlots.indices) {
                    val curSlot = hotbarSlots[i]
                    if (insertIntoEmpty(toInsert, curSlot)) inserted = true
                    if (toInsert.isEmpty) break
                }
            }
        }
        return inserted
    }
}
