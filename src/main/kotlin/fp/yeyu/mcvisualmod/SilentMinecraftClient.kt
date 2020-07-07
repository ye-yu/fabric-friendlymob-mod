package fp.yeyu.mcvisualmod

import fp.yeyu.mcvisualmod.mobs.renderer.VindorRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SilentMinecraftClient : ClientModInitializer {
    private val logger: Logger = LogManager.getLogger()
    override fun onInitializeClient() {
        logger.info("Mod is loaded. [Client-side]")

        EntityRendererRegistry.INSTANCE.register(
            SilentMinecraft.Mobs.VINDOR.entry
        ) { dispatcher, _ ->
            VindorRenderer(dispatcher)
        }
    }
}
