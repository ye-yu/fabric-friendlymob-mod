package fp.yeyu.mcvisualmod

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SilentMinecraft : ModInitializer {
    private val logger: Logger = LogManager.getLogger()
    override fun onInitialize() {
        logger.info("Mod is loaded. [Main]")
    }
}