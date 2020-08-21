package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard.CustomRecipe
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.provider.DoubleProvider
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
) : ClientInventoryHandler<T>(type, syncId, playerInventory), IntegerProvider, DoubleProvider, RecipeProvider, RecipeClickListener {

    var recipeContext = RecipeContext(null)
    var experience: Double = 0.0

    override fun getInteger(name: String): Int {
        return when (name) {
            WizardPackets.SCROLLBAR -> recipeContext.level
            WizardPackets.LEVEL -> recipeContext.level
            else -> throw IllegalArgumentException("Handler does not provide integer for $name")
        }
    }

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        when (action) {
            WizardPackets.SYNC_PACKET -> recipeContext.sync(buf)
            WizardPackets.EXP_BAR -> {
                experience = buf.readDouble()
                Logger.info("Got wizard experience level of $experience")
            }
            else -> super.onServer2Client(action, context, buf)
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

    override fun getDouble(name: String): Double {
        return when (name) {
            WizardPackets.EXP_BAR -> experience
            else -> throw IllegalArgumentException("Handler does not provide integer for $name")
        }
    }
}