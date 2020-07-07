package fp.yeyu.mcvisualmod

import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// TODO: Custom background sound
// TODO: Custom resource pack/visual edit

class SilentMinecraftClient : ClientModInitializer {
    private val logger: Logger = LogManager.getLogger()
    override fun onInitializeClient() {
        logger.info("Mod is loaded. [Client-side]")
    }
}
