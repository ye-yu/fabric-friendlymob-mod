package fp.yeyu.monsterfriend.screens.evione

import fp.yeyu.monsterfriend.mobs.entity.Evione
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import io.github.yeyu.packet.ScreenPacket
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

class EvioneServerScreenHandler<T : ScreenRendererHandler>(
        type: ScreenHandlerType<T>,
        syncId: Int,
        playerInventory: PlayerInventory,
        private val evione: Evione
) : ServerInventoryHandler<T>(type, syncId, playerInventory) {

    var lastProgress = 0

    init {
        super.blockInventory = evione.getInventory()

        constrainedSlots[playerInventory.size()].insertPredicate = {
            constrainedSlots[playerInventory.size()].stack.isEmpty // only insert when current stack is empty
                    && !Evione.isEssence(it.item) // cannot insert non-essence
        }

        constrainedSlots[playerInventory.size()].setCapacity(1) // can only insert one item

        constrainedSlots[playerInventory.size() + 1].insertPredicate = {
            Evione.isEssence(it.item) // can only insert essence
        }

        constrainedSlots[playerInventory.size() + 2].insertPredicate = { false } // cannot insert at all
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()
        if (!clientHasInited) return
        val progress = evione.getSynthesisProgress().toInt()

        if (lastProgress == progress) return // don't send if it is not different
        ScreenPacket.sendPacket(syncId, EvionePacket.SET_PROGRESS, false, playerInventory.player as ServerPlayerEntity) {
            it.writeInt(progress)
        }
        lastProgress = progress
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        evione.endInteraction()
    }
}