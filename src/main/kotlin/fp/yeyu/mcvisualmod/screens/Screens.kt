package fp.yeyu.mcvisualmod.screens

import fp.yeyu.mcvisualmod.SilentMinecraft
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

class Screens {
    companion object {
        @JvmStatic
        fun <T : ScreenHandler> register(name: String, entry: (Int, PlayerInventory) -> T): ScreenHandlerType<T> {
            return ScreenHandlerRegistry.registerSimple<T>(
                Identifier(SilentMinecraft.NAMESPACE, name),
                entry
            )
        }

        @JvmStatic
        val VINDOR_SCREEN: ScreenHandlerType<VindorGUI> =
            register("vindor_screen") { syncId: Int, player: PlayerInventory ->
                VindorGUI(
                    syncId,
                    player,
                    net.minecraft.screen.ScreenHandlerContext.EMPTY,
                    null
                )
            }
    }
}