package fp.yeyu.monsterfriend.screens.widget

import io.github.cottonmc.cotton.gui.ValidatedSlot
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.inventory.Inventory

class WListeningItemSlot(inventory: Inventory?, startIndex: Int) :
    WItemSlot(inventory, startIndex, 1, 1, false) {

    override fun createSlotPeer(inventory: Inventory?, index: Int, x: Int, y: Int): ValidatedSlot {
        return ListeningSlot(inventory, index, x, y)
    }
}