package fp.yeyu.monsterfriend

import fp.yeyu.monsterfriend.mobs.MobRegistry
import fp.yeyu.monsterfriend.mobs.egg.EvioneEgg
import fp.yeyu.monsterfriend.mobs.egg.VindorEgg
import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.utils.ConfigFile
import net.fabricmc.api.ModInitializer
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class BefriendMinecraft : ModInitializer {
    companion object {
        const val NAMESPACE = "friendlymob"
        val LOGGER: Logger = LogManager.getLogger()

        fun registerItem(id: String, instantiation: Item) {
            Registry.register(
                Registry.ITEM, Identifier(NAMESPACE, id), instantiation
            )

        }
    }

    override fun onInitialize() {
        LOGGER.info("Mod is loaded. [Main]")
        Screens.registerServer()
        registerItem(
            VindorEgg.NAME,
            VindorEgg(
                MobRegistry.vindor.entityType,
                3407872,
                12369084,
                Item.Settings().maxDamage(1).group(ItemGroup.MISC)
            )
        )
        registerItem(
            EvioneEgg.NAME,
            EvioneEgg(
                MobRegistry.evione.entityType,
                0xFFF5500,
                0xF006634,
                Item.Settings().maxDamage(1).group(ItemGroup.MISC)
            )
        )

        MobRegistry.registerMobs(false)
        ConfigFile.visit()
    }
}