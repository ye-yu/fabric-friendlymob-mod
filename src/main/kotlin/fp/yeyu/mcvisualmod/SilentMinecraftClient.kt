package fp.yeyu.mcvisualmod

import fp.yeyu.mcvisualmod.mobs.renderer.VindorRenderer
import fp.yeyu.mcvisualmod.screens.Screens
import fp.yeyu.mcvisualmod.screens.VindorClientScreen
import fp.yeyu.mcvisualmod.screens.VindorGUI
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
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

        ScreenRegistry.register<VindorGUI, VindorClientScreen>(
            Screens.VINDOR_SCREEN
        ) { gui: VindorGUI?, inventory: PlayerInventory, title: Text? ->
            VindorClientScreen(
                gui!!,
                inventory.player,
                title!!
            )
        }
    }
}
