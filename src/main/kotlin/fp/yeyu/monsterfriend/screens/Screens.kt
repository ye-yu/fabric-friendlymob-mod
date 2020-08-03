package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.screens.evione.EvioneClientScreen
import fp.yeyu.monsterfriend.screens.evione.EvioneClientScreenHandler
import fp.yeyu.monsterfriend.screens.vindor.ClientVindorScreenHandler
import fp.yeyu.monsterfriend.screens.vindor.VindorClientScreen
import io.github.yeyu.gui.handler.ScreenRendererHandler
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object Screens {
    lateinit var VINDOR_SCREEN: ScreenHandlerType<ScreenRendererHandler>

    lateinit var EVIONE_SCREEN: ScreenHandlerType<ScreenRendererHandler>

    fun registerServer() {
        VINDOR_SCREEN =
            register("vindor_screen") { syncId: Int, player: PlayerInventory ->
                ClientVindorScreenHandler(
                    VINDOR_SCREEN,
                    syncId,
                    player
                )
            }

        EVIONE_SCREEN =
            register("evione_screen") { syncId: Int, player: PlayerInventory ->
                EvioneClientScreenHandler(
                    EVIONE_SCREEN,
                    syncId,
                    player
                )
            }

    }

    fun registerScreens() {
        ScreenRegistry.register(VINDOR_SCREEN) { screenRendererHandler, playerInventory, text ->
            VindorClientScreen(screenRendererHandler, playerInventory, text)
        }

        ScreenRegistry.register(EVIONE_SCREEN) { screenRendererHandler, playerInventory, text ->
            EvioneClientScreen(screenRendererHandler, playerInventory, text)
        }
    }

    private fun <T : ScreenHandler> register(name: String, entry: (Int, PlayerInventory) -> T): ScreenHandlerType<T> {
        return ScreenHandlerRegistry.registerSimple<T>(
            Identifier(BefriendMinecraft.NAMESPACE, name),
            entry
        )
    }
}