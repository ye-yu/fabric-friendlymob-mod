package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.BefriendMinecraft
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.reflect.KFunction3

object Screens {
    val vindorScreen: ScreenHandlerType<VindorGUI> =
        register("vindor_screen") { syncId: Int, player: PlayerInventory ->
            VindorGUI(
                syncId,
                player,
                net.minecraft.screen.ScreenHandlerContext.EMPTY,
                null
            )
        }

    val evioneScreen: ScreenHandlerType<EvioneGUI> = register("evione_screen") { syncId: Int, player: PlayerInventory ->
        EvioneGUI(
            syncId,
            player,
            net.minecraft.screen.ScreenHandlerContext.EMPTY,
            null
        )
    }

    fun registerScreens() {
        register(vindorScreen, ::VindorClientScreen)
        register(evioneScreen, ::EvioneClientScreen)
    }

    private fun <T: SyncedGuiDescription, V: CottonInventoryScreen<T>> register(
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