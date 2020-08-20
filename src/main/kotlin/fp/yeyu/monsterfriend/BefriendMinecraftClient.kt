package fp.yeyu.monsterfriend

import fp.yeyu.monsterfriend.mobs.MobRegistry
import fp.yeyu.monsterfriend.screens.Screens
import io.github.yeyu.util.Logger
import net.fabricmc.api.ClientModInitializer

class BefriendMinecraftClient : ClientModInitializer {

    override fun onInitializeClient() {
        Logger.info("[Client-side] Mod is loaded.")
        MobRegistry.registerMobRenderers()
        Screens.registerClientScreens()
        Particle.registerClient()
    }
}
