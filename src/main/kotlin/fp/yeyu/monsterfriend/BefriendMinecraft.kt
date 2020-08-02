package fp.yeyu.monsterfriend

import fp.yeyu.monsterfriend.item.ItemRegistry
import fp.yeyu.monsterfriend.mobs.MobRegistry
import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.utils.ConfigFile
import io.github.yeyu.util.Logger
import net.fabricmc.api.ModInitializer

class BefriendMinecraft : ModInitializer {
    companion object {
        const val NAMESPACE = "friendlymob"
    }

    override fun onInitialize() {
        Logger.info("Mod is loaded. [Main]")
        Screens.registerServer()
        MobRegistry.registerMobs(false)
        MobRegistry.registerEggs()
        ItemRegistry.registerItem()
        ConfigFile.visit()
    }
}