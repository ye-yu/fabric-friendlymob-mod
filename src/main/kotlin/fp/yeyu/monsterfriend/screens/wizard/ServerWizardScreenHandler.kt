package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

class ServerWizardScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    private val wizard: Wizard
) : ServerInventoryHandler<T>(type, syncId, playerInventory) {

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
}