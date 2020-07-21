package fp.yeyu.monsterfriend.packets

import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

enum class PacketHandlers(val toServer: Boolean, val handler: (PacketContext, PacketByteBuf) -> Unit) {
    SCREEN_S2C(false, Handlers::server2ClientHandler),
    SCREEN_C2S(true, Handlers::client2ServerHandler);

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
                    LOGGER.info("[Server] Registered packet ${it.id}")
                    ServerSidePacketRegistry.INSTANCE.register(it.id, it.handler)
                } else if (!it.toServer && client) {
                    LOGGER.info("[Client] Registered packet ${it.id}")
                    ClientSidePacketRegistry.INSTANCE.register(it.id, it.handler)
                }
            }
        }

        private val LOGGER: Logger = LogManager.getLogger()
    }

    object Handlers {
        fun server2ClientHandler(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
            val currentScreen = MinecraftClient.getInstance().currentScreen ?: return
            check(currentScreen is CottonInventoryScreen<*>) { "Current screen is not an instance of CottonInventoryScreen!" }
            val screenHandler = currentScreen.screenHandler
            check(screenHandler is ScreenDescriptionPacketListener) { "Current screen description is not an instance implementing ScreenPacketListener!" }
            screenHandler.onServer2Client(packetContext, packetByteBuf)
        }

        fun client2ServerHandler(packetContext: PacketContext, packetByteBuf: PacketByteBuf) {
            val screenHandler = packetContext.player.currentScreenHandler
            check(screenHandler is ScreenDescriptionPacketListener) { "Current screen description is not an instance implementing ScreenPacketListener!" }
            screenHandler.onClient2Server(packetContext, packetByteBuf)
        }
    }

}
