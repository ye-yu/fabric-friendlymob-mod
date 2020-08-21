package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard
import fp.yeyu.monsterfriend.mobs.entity.WizardUtil
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import io.github.yeyu.gui.handler.inventory.utils.SlotActionType
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
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
    var lastExpBar: Int

    init {
        super.blockInventory = suggestedCrafts
        constrainedSlots[playerInventory.size() + 4].insertPredicate = { false }
        lastExpBar = wizard.experience
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
        ScreenPacket.sendPacket(syncId, WizardPackets.EXP_BAR, false, playerInventory.player as ServerPlayerEntity) {
            it.writeDouble(
                wizard.remainingExp.toDouble() / (wizard.remainingExp.toDouble() + WizardUtil.LevelUtil.getRemainingToLevelUp(
                    wizard.experience
                ))
            )
        }
    }

    override fun sendContentUpdates() {
        refreshContent()
        super.sendContentUpdates()
        if (lastExpBar == wizard.experience) return
        lastExpBar = wizard.experience
        ScreenPacket.sendPacket(syncId, WizardPackets.EXP_BAR, false, playerInventory.player as ServerPlayerEntity) {
            it.writeDouble(
                wizard.remainingExp.toDouble() / (wizard.remainingExp.toDouble() + WizardUtil.LevelUtil.getRemainingToLevelUp(
                    wizard.experience
                ))
            )
        }
        recipeContext.level = wizard.currentLevel
        recipeContext.sync(this, WizardPackets.SYNC_PACKET, playerInventory.player as ServerPlayerEntity)
    }

    private fun refreshContent() {
        val item1 = constrainedSlots[playerInventory.size()].stack
        val item2 = constrainedSlots[playerInventory.size() + 1].stack
        val flower = constrainedSlots[playerInventory.size() + 2].stack
        val potion = constrainedSlots[playerInventory.size() + 3].stack

        val currentStack = constrainedSlots[playerInventory.size() + 4].stack
        val craftingStack = wizard.prepareCraft(item1, item2, flower, potion)
        if (canStacksCombine(currentStack, craftingStack)) return
        constrainedSlots[playerInventory.size() + 4].stack = craftingStack.copy()
    }

    private fun matchSlot(item: ItemStack): Int? {
        for (index in 0 until playerInventory.size() + 4) {
            val stack = constrainedSlots[index].stack
            if (canStacksCombine(item, stack)) return index
        }
        return null
    }

    override fun onRecipeButtonClick(slot: Int) {
        if (!getCursorStack().isEmpty) return
        val (_, item1, item2, flower, potion, _) = recipeContext.learntRecipe.recipes[slot]

        val slot1 = matchSlot(item1) ?: return
        val slot2 = matchSlot(item2) ?: return
        val slot3 = matchSlot(flower) ?: return
        val slot4 = matchSlot(potion) ?: return

        swapSlot(slot1, playerInventory.size())
        swapSlot(slot2, playerInventory.size() + 1)
        swapSlot(slot3, playerInventory.size() + 2)
        swapSlot(slot4, playerInventory.size() + 3)
    }

    private fun swapSlot(a: Int, b: Int) {
        val tempStack = constrainedSlots[a].stack
        constrainedSlots[a].stack = constrainedSlots[b].stack
        constrainedSlots[b].stack = tempStack
    }

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) =
        if (action == WizardPackets.RECIPE_CLICK) onRecipeButtonClick(buf.readInt())
        else super.onClient2Server(action, context, buf)

    override fun onSlotEvent(slotNumber: Int, action: SlotActionType): Boolean {
        if (super.onSlotEvent(slotNumber, action)) {
            if (action == SlotActionType.PICKUP_ALL || action == SlotActionType.PICKUP_HALF) {
                if (slotNumber != playerInventory.size() + 4) return true
                requestCraft()
                return true
            } else return true
        } else {
            return false
        }
    }

    private fun requestCraft() {
        val item1 = constrainedSlots[playerInventory.size()].stack
        val item2 = constrainedSlots[playerInventory.size() + 1].stack
        val flower = constrainedSlots[playerInventory.size() + 2].stack
        val potion = constrainedSlots[playerInventory.size() + 3].stack

        val craftReward = wizard.getCraftReward(item1, item2, flower, potion)
        if (craftReward > 0) {
            val playerEntity = playerInventory.player
            wizard.craftSuccessful(craftReward)
            playerEntity.world.spawnEntity(
                ExperienceOrbEntity(
                    playerEntity.world,
                    playerEntity.x,
                    playerEntity.y + 0.5,
                    playerEntity.z + 0.5,
                    craftReward
                )
            )
        }

        item1.decrement(1)
        item2.decrement(1)
        flower.decrement(1)
        potion.decrement(1)

        refreshContent()
    }
}