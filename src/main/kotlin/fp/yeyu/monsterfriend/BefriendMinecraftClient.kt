package fp.yeyu.monsterfriend

import fp.yeyu.monsterfriend.mobs.MobRegistry
import fp.yeyu.monsterfriend.packets.PacketHandlers
import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.screens.VindorClientScreen
import fp.yeyu.monsterfriend.screens.VindorGUI
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class BefriendMinecraftClient : ClientModInitializer {

    companion object {
        val LOGGER: Logger = LogManager.getLogger()
    }

    private val logger: Logger = LogManager.getLogger()
    override fun onInitializeClient() {
        logger.info("Mod is loaded. [Client-side]")

        ScreenRegistry.register<VindorGUI, VindorClientScreen>(
            Screens.VINDOR_SCREEN
        ) { gui: VindorGUI?, inventory: PlayerInventory, title: Text? ->
            VindorClientScreen(
                gui!!,
                inventory.player,
                title!!
            )
        }

        PacketHandlers.init(true)
        MobRegistry.init(true)
    }
}
