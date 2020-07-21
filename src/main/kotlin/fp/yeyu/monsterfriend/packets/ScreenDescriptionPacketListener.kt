package fp.yeyu.monsterfriend.packets

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf

interface ScreenDescriptionPacketListener {
    fun onServer2Client(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
        val screenContext = getScreenPacketContext(packetByteBuf)
        check(screenContext.syncId == getSyncId()) { "Conflicting syncId upon receiving server action of ${screenContext.actionIdentifier}!" }
        getS2CListeners().getOrDefault(screenContext.actionIdentifier) { }(packetByteBuf)
    }

    fun getSyncId(): Int

    fun getS2CListeners(): HashMap<String, (PacketByteBuf) -> Unit>

    fun onClient2Server(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
        val screenContext = getScreenPacketContext(packetByteBuf)
        check(screenContext.syncId == getSyncId()) { "Conflicting syncId upon receiving client action of ${screenContext.actionIdentifier}!" }
        getC2SListeners().getOrDefault(screenContext.actionIdentifier) { }(packetByteBuf)
    }

    fun getC2SListeners(): HashMap<String, (PacketByteBuf) -> Unit>

    fun getScreenPacketContext(packetByteBuf: PacketByteBuf): ScreenPacketContext {
        val syncId = packetByteBuf.readInt()
        val actionId = packetByteBuf.readString()
        return ScreenPacketContext(syncId, actionId)
    }

    fun createWrappedPacketBuffer(syncId: Int, actionIdentifier: String): PacketByteBuf {
        return ScreenPacketContext(syncId, actionIdentifier).createPacketBuffer()
    }
}

class ScreenPacketContext(var syncId: Int, var actionIdentifier: String) {
    fun createPacketBuffer(): PacketByteBuf {
        val packetByteBuf = PacketByteBuf(Unpooled.buffer())
        packetByteBuf.writeInt(syncId)
        packetByteBuf.writeString(actionIdentifier)
        return packetByteBuf
    }
}
