package fp.yeyu.monsterfriend

import fp.yeyu.monsterfriend.mobs.MobRegistry
import fp.yeyu.monsterfriend.screens.Screens
import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class BefriendMinecraftClient : ClientModInitializer {

    companion object {
        val LOGGER: Logger = LogManager.getLogger()
    }

    override fun onInitializeClient() {
        LOGGER.info("[Client-side] Mod is loaded.")
        MobRegistry.registerMobs(true)
        Screens.registerScreens()
    }
}
