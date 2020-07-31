package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.yeyu.gui.handler.ScreenRendererHandler
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction3

object Screens {
    var VINDOR_SCREEN: ScreenHandlerType<ScreenRendererHandler>? = null

    var EVIONE_SCREEN: ScreenHandlerType<EvioneScreenDescription> =
        register("evione_screen") { syncId: Int, player: PlayerInventory ->
            EvioneScreenDescription(
                syncId,
                player,
                net.minecraft.screen.ScreenHandlerContext.EMPTY,
                null
            )
        }

    fun registerServer() {
        VINDOR_SCREEN =
            register("vindor_screen") { syncId: Int, player: PlayerInventory ->
                ClientVindorScreenHandler(
                    VINDOR_SCREEN!!,
                    syncId,
                    player
                )
            }
    }

    fun registerScreens() {
        ScreenRegistry.register(VINDOR_SCREEN!!) {
            screenRendererHandler, playerInventory, text -> VindorClientScreen(screenRendererHandler, playerInventory, text)
        }
        register(EVIONE_SCREEN, ::EvioneClientScreen)
    }

    private fun <T : SyncedGuiDescription, V : CottonInventoryScreen<T>> register(
        screen: ScreenHandlerType<T>,
        factory: KFunction3<@ParameterName(name = "description") T, @ParameterName(
            name = "player"
        ) PlayerInventory, @ParameterName(name = "title") Text, V>
    ) {
        ScreenRegistry.register<T, V>(screen, factory)
    }

    private fun <T : ScreenHandler> register(name: String, entry: (Int, PlayerInventory) -> T): ScreenHandlerType<T> {
        return ScreenHandlerRegistry.registerSimple<T>(
            Identifier(BefriendMinecraft.NAMESPACE, name),
            entry
        )
    }
}