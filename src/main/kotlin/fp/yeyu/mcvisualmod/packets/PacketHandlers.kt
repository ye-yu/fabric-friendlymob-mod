package fp.yeyu.mcvisualmod.packets

import fp.yeyu.mcvisualmod.SilentMinecraft
import fp.yeyu.mcvisualmod.screens.VindorGUI
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
    VINDOR_SEND_TEXT(true, ::parseVindorText);

    val id: Identifier = Identifier(SilentMinecraft.NAMESPACE, this.name.toLowerCase())

    fun validate(world: World): Boolean {
        return toServer && world.isClient() || !toServer && !world.isClient
    }

    fun send(world: World, buf: PacketByteBuf, player: PlayerEntity?) {
        if (toServer && world.isClient()) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
        } else if (!toServer && !world.isClient) {
            if (player == null) throw IllegalArgumentException("Player must not be null.")
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, id, buf)
        } else {
            throw IllegalArgumentException("Got world.isClient: ${world.isClient} but the packet is for ${if (toServer) "server" else "client"}.")
        }
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger()
    }
}

fun parseVindorText(context: PacketContext, buf: PacketByteBuf) {
    val text = buf.readString()
    val sh = context.player.currentScreenHandler
    if (sh !is VindorGUI) {
        PacketHandlers.LOGGER.warn("[Server] Expected current screen handler as VindorGUI but got ${sh::class.simpleName} instead")
        PacketHandlers.LOGGER.trace(Throwable())
    }
    val vindor = (sh as VindorGUI).vindor!!
    if (text.length >= VindorGUI.MAX_TEXT_LENGTH) return
    PacketHandlers.LOGGER.info("[Server] Writing $text to vindor entity.")
    vindor.setSenderMessage(text)
}