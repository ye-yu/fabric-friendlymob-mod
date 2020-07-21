package fp.yeyu.monsterfriend.packets

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.screens.VindorScreenDescription
import fp.yeyu.monsterfriend.statics.mutable.OpenedScreen
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

    val id: Identifier = Identifier(BefriendMinecraft.NAMESPACE, this.name.toLowerCase())

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
        fun registerPackets(client: Boolean) {
            values().forEach {
                if (it.toServer && !client) {
                    ServerSidePacketRegistry.INSTANCE.register(it.id, it.handler)
                } else if (!it.toServer && client) {
                    ClientSidePacketRegistry.INSTANCE.register(it.id, it.handler)
                }
            }
        }

        val LOGGER: Logger = LogManager.getLogger()
    }
}

fun initVindorText(@Suppress("UNUSED_PARAMETER") ignored: PacketContext, packetByteBuf: PacketByteBuf) {
    val screen = OpenedScreen.unset()
    (screen as VindorScreenDescription).initText(packetByteBuf.readString())
}

fun requestVindorText(packetContext: PacketContext, @Suppress("UNUSED_PARAMETER") packetByteBuf: PacketByteBuf) {
    val currentScreenHandler = packetContext.player.currentScreenHandler
    if (currentScreenHandler !is VindorScreenDescription) {
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
    if (sh !is VindorScreenDescription) {
        PacketHandlers.LOGGER.warn("[Server] Expected current screen handler as VindorGUI but got ${sh::class.simpleName} instead!")
        PacketHandlers.LOGGER.trace(Throwable())
        return
    }
    sh.initText(text)
}