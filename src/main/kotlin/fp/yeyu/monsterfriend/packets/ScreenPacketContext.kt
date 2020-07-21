package fp.yeyu.monsterfriend.packets

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf

class ScreenPacketContext(var syncId: Int, var actionIdentifier: String) {
    fun createPacketBuffer(): PacketByteBuf {
        val packetByteBuf = PacketByteBuf(Unpooled.buffer())
        packetByteBuf.writeInt(syncId)
        packetByteBuf.writeString(actionIdentifier)
        return packetByteBuf
    }
}
