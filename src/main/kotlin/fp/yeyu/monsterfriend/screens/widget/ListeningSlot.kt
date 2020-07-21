package fp.yeyu.monsterfriend.screens.widget

import io.github.cottonmc.cotton.gui.ValidatedSlot
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class ListeningSlot(inventory: Inventory?, index: Int, x: Int, y: Int) : ValidatedSlot(inventory, index, x, y) {

    private var listener: (ItemStack, ItemStack) -> Unit = { _: ItemStack, _: ItemStack -> }
    override fun onStackChanged(originalItem: ItemStack, incomingStack: ItemStack) {
        println("calling stack change")
        super.onStackChanged(originalItem, incomingStack)
        listener(originalItem, incomingStack)
    }

    fun setListener(f: (ItemStack, ItemStack) -> Unit) {
        listener = f
        println("successfully set listener")
    }
}