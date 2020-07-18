package fp.yeyu.mcvisualmod.packets

import fp.yeyu.mcvisualmod.SilentMinecraft
import fp.yeyu.mcvisualmod.screens.Magic
import fp.yeyu.mcvisualmod.screens.VindorGUI
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

enum class PacketHandlers(val toServer: Boolean, val handler: (PacketContext, PacketByteBuf) -> Unit) {
    VINDOR_SEND_TEXT(true, ::parseVindorText),
    VINDOR_REQUEST_TEXT(true, ::requestVindorText),
    VINDOR_INIT_CLIENT_TEXT(false, ::initVindorText);

    val id: Identifier = Identifier(SilentMinecraft.NAMESPACE, this.name.toLowerCase())

    private fun validate(world: World, player: PlayerEntity?) {
        if (!world.isClient && !toServer && player == null) throw IllegalArgumentException("Player must not be null.")
        if (toServer && world.isClient() || !toServer && !world.isClient) return
        throw IllegalArgumentException("Got world.isClient: ${world.isClient} but the packet is for ${if (toServer) "server" else "client"}.")
    }

    fun send(world: World, buf: PacketByteBuf, player: PlayerEntity?) {
        validate(world, player)
        if (toServer) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
        } else {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, id, buf)
        }
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger()
    }
}

fun initVindorText(@Suppress("UNUSED_PARAMETER") ignored: PacketContext, packetByteBuf: PacketByteBuf) {
    val screen = Magic.pop(Magic.Key.OPEN_SCREEN)
    (screen as VindorGUI).initText(packetByteBuf.readString())

}

fun requestVindorText(packetContext: PacketContext, @Suppress("UNUSED_PARAMETER") packetByteBuf: PacketByteBuf) {
    val currentScreenHandler = packetContext.player.currentScreenHandler
    if (currentScreenHandler !is VindorGUI) {
        PacketHandlers.LOGGER.warn("Player screen is currently not a VindorGUI instance!")
        PacketHandlers.LOGGER.trace(Throwable())
        return
    }
    val senderMessage = currentScreenHandler.vindor!!.getSenderMessage()
    val buf = PacketByteBuf(Unpooled.buffer())
    buf.writeString(senderMessage)
    PacketHandlers.VINDOR_INIT_CLIENT_TEXT.send(packetContext.player.world, buf, packetContext.player)
}

fun parseVindorText(context: PacketContext, buf: PacketByteBuf) {
    val text = buf.readString()
    val sh = context.player.currentScreenHandler
    if (sh !is VindorGUI) {
        PacketHandlers.LOGGER.warn("[Server] Expected current screen handler as VindorGUI but got ${sh::class.simpleName} instead!")
        PacketHandlers.LOGGER.trace(Throwable())
        return
    }
    sh.initText(text)
}