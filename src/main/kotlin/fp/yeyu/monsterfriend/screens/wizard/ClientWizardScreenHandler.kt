package fp.yeyu.monsterfriend.screens.wizard

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.provider.IntegerProvider
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import java.lang.IllegalArgumentException

class ClientWizardScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(type, syncId, playerInventory), IntegerProvider {

    var recipeContext = RecipeContext(null)

    override fun getInteger(name: String): Int {
        if (name.equals(WizardPackets.SCROLLBAR, true)) {
            return recipeContext.level
        }
        throw IllegalArgumentException("Handler does not provide integer for $name")
    }

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        if (action.equals(WizardPackets.SYNC_PACKET, true)) {
            recipeContext.sync(buf)
        } else {
            super.onServer2Client(action, context, buf)
        }
    }
}