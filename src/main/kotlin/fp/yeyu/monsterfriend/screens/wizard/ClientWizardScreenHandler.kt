package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard.CustomRecipe
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.provider.BooleanProvider
import io.github.yeyu.gui.handler.provider.DoubleProvider
import io.github.yeyu.gui.handler.provider.IntegerProvider
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

class ClientWizardScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(type, syncId, playerInventory), IntegerProvider, DoubleProvider, BooleanProvider,
    RecipeProvider,
    RecipeClickListener {

    var recipeContext = RecipeContext(null)
    var experience: Double = 0.0

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        when (action) {
            WizardPackets.SYNC_PACKET -> recipeContext.sync(buf)
            WizardPackets.EXP_BAR -> experience = buf.readDouble()
            else -> super.onServer2Client(action, context, buf)
        }
    }

    override fun getRecipe(slot: Int): CustomRecipe {
        return recipeContext.learntRecipe.recipes[slot]
    }

    override fun onRecipeButtonClick(slot: Int) {
        ScreenPacket.sendPacket(syncId, WizardPackets.RECIPE_CLICK, true, null) {
            it.writeInt(slot)
        }
    }

    override fun getInteger(name: String): Int {
        return when (name) {
            WizardPackets.SCROLLBAR -> recipeContext.level
            WizardPackets.LEVEL -> recipeContext.level
            else -> throw IllegalArgumentException("Handler does not provide integer for $name")
        }
    }

    override fun getDouble(name: String): Double {
        return when (name) {
            WizardPackets.EXP_BAR -> experience
            else -> throw IllegalArgumentException("Handler does not provide double for $name")
        }
    }

    override fun getBoolean(name: String): Boolean {
        return when (name) {
            WizardPackets.EXP_BAR -> recipeContext.level != 5
            else -> throw IllegalArgumentException("Handler does not provide boolean for $name")
        }
    }
}