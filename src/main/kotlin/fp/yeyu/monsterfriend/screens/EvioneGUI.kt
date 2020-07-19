package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.mobs.entity.Evione
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
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

    init {
        val root = WGridPanel()
        setRootPanel(root)

        if (evione != null) {
            val inv = evione.getInventory()
            val synthesisStack = inv.getStack(0)
            val producedStack = inv.getStack(1)
            setBlockInventoryIfNotEmpty(0, synthesisStack)
            setBlockInventoryIfNotEmpty(2, producedStack)
        }

        IntStream.range(0, SIZE).forEach{
            val slot = WItemSlot.of(blockInventory, it)
            val label = createCenteredLabel(WIDGET_LABEL[it])
            root.add(slot, 1 + 3 * it, 1)
            root.add(label, 1 + 3 * it, 2)
        }

        val playerSlot = createPlayerInventoryPanel()
        root.add(playerSlot, 0, 3)
        root.setSize(playerSlot.width, 100)
        root.validate(this)
    }

    private fun setBlockInventoryIfNotEmpty(slot: Int, itemStack: ItemStack?) {
        if (itemStack == null || itemStack.isEmpty) return
        blockInventory.setStack(slot, itemStack)
    }

    companion object {
        const val SIZE = 3
        val WIDGET_LABEL = arrayOf(
            "Target", "Speed Up", "Output"
        )
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
