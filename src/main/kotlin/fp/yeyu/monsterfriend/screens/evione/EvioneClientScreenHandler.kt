package fp.yeyu.monsterfriend.screens.evione

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.provider.DoubleProvider
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

class EvioneClientScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(
    type,
    syncId,
    playerInventory
), DoubleProvider {

    var progress = 0
    override fun getDouble(name: String): Double {
        if (name.equals(EvionePacket.PROGRESS_NAME, true)) {
            return progress / 100.0
        }
        throw IllegalArgumentException("Client does not handle $name!")
    }

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onServer2Client(action, context, buf)
        if (action.equals(EvionePacket.SET_PROGRESS, true)) {
            progress = buf.readInt()
        }
    }
}