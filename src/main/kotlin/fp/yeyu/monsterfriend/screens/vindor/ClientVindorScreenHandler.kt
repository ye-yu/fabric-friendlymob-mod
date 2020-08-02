package fp.yeyu.monsterfriend.screens.vindor

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.inventory.ClientInventoryHandler
import io.github.yeyu.gui.handler.listener.StringListener
import io.github.yeyu.gui.handler.provider.StringProvider
import io.github.yeyu.packet.ScreenPacket
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import java.lang.IllegalArgumentException

class ClientVindorScreenHandler<T : ScreenRendererHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory
) : ClientInventoryHandler<T>(type, syncId, playerInventory), StringListener, StringProvider {

    var vindorText = ""

    override fun onServer2Client(action: String, context: PacketContext, buf: PacketByteBuf) {
        super.onServer2Client(action, context, buf)
        if (action.equals(VindorPackets.VINDOR_TEXT_UPDATE, true)) {
            vindorText = buf.readString()
        }
    }

    override fun onStringChange(str: String, name: String) {
        if (name.equals(VindorPackets.VINDOR_TEXT_FIELD_NAME, true)) {
            vindorText = str
            ScreenPacket.sendPacket(syncId, VindorPackets.VINDOR_TEXT_UPDATE, true, null) {
                it.writeString(str)
            }
        } else {
            throw IllegalArgumentException("Client handler does not listen to $name.")
        }
    }

    override fun getString(name: String): String {
        if (name.equals(VindorPackets.VINDOR_TEXT_FIELD_NAME, true)) {
            return vindorText
        }
        throw IllegalArgumentException("Client handler does not provide string for $name.")
    }
}