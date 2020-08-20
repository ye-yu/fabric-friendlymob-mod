package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard.CustomRecipe
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.provider.IntegerProvider
import io.github.yeyu.packet.ScreenPacket
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import java.lang.IllegalArgumentException

class ClientWizardScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(type, syncId, playerInventory), IntegerProvider, RecipeProvider, RecipeClickListener {

    var recipeContext = RecipeContext(null)

    override fun getInteger(name: String): Int {
        if (name.equals(WizardPackets.SCROLLBAR, true)) {
            return recipeContext.level // ??
        } else if (name.equals(WizardPackets.LEVEL, true)) {
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

    override fun getRecipe(slot: Int): CustomRecipe {
        return recipeContext.learntRecipe.recipes[slot]
    }

    override fun onRecipeButtonClick(slot: Int) {
        if (recipeContext.learntRecipe.recipes[slot].toCraft.isEmpty) return
        Logger.info("Sending recipe click of $slot")

        ScreenPacket.sendPacket(syncId, WizardPackets.RECIPE_CLICK, true, null) {
            it.writeInt(slot)
        }
    }
}