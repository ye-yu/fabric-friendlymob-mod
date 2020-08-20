package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

class ServerWizardScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    private val wizard: Wizard
) : ServerInventoryHandler<T>(type, syncId, playerInventory), RecipeClickListener {

    private val suggestedCrafts = SimpleInventory(5)
    private val recipeContext = RecipeContext(wizard.learntRecipe)
    init {
        super.blockInventory = suggestedCrafts
        constrainedSlots[playerInventory.size() + 4].insertPredicate = { false }
    }

    override fun close(player: PlayerEntity) {
        for (index in 0 until suggestedCrafts.size()) {
            val stack = suggestedCrafts.getStack(index)
            if (stack.isEmpty) continue
            player.dropItem(stack, false)
        }
        wizard.customer = null
    }

    override fun clientHasInit() {
        super.clientHasInit()
        recipeContext.sync(this, WizardPackets.SYNC_PACKET, playerInventory.player as ServerPlayerEntity)
    }

    override fun onRecipeButtonClick(slot: Int) {
        Logger.info("Got click at slot $slot")
    }

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) =
        if (action == WizardPackets.RECIPE_CLICK) onRecipeButtonClick(buf.readInt())
        else super.onClient2Server(action, context, buf)
}