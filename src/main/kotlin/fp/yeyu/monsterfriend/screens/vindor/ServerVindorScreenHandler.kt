package fp.yeyu.monsterfriend.screens.vindor

import fp.yeyu.monsterfriend.mobs.entity.Vindor
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ServerInventoryHandler
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

class ServerVindorScreenHandler<T : ScreenRendererHandler>(
        type: ScreenHandlerType<T>,
        syncId: Int,
        playerInventory: PlayerInventory,
        val vindor: Vindor
) : ServerInventoryHandler<T>(type, syncId, playerInventory) {

    init {
        super.blockInventory = vindor.getInventory()
    }

    override fun initBlockInventory() {
        super.initBlockInventory()
        constrainedSlots[playerInventory.size() + 1].insertPredicate = { false }
    }

    override fun clientHasInit() {
        super.clientHasInit()
        ScreenPacket.sendPacket(syncId, VindorPackets.VINDOR_TEXT_UPDATE, false,
                playerInventory.player as ServerPlayerEntity
        ) {
            it.writeString(vindor.senderMsg)
        }
    }

    override fun onClient2Server(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onClient2Server(action, context, buf)
        if (action.equals(VindorPackets.VINDOR_TEXT_UPDATE, true)) {
            vindor.senderMsg = buf.readString()
        }
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        val sendStack = blockInventory!!.getStack(0)
        val receiveStack = blockInventory!!.getStack(1)
        vindor.finishTrading(sendStack, receiveStack) // todo: cant vindor just read from his own inventory?
    }

}