package fp.yeyu.monsterfriend.screens

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.screens.evione.EvioneClientScreen
import fp.yeyu.monsterfriend.screens.evione.EvioneClientScreenHandler
import fp.yeyu.monsterfriend.screens.vindor.ClientVindorScreenHandler
import fp.yeyu.monsterfriend.screens.vindor.VindorClientScreen
import fp.yeyu.monsterfriend.screens.wizard.ClientWizardScreenHandler
import fp.yeyu.monsterfriend.screens.wizard.WizardClientScreen
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.util.Logger
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

enum class Screens(
    val handlerFactory: (ScreenHandlerType<ScreenRendererHandler>, Int, PlayerInventory) -> ScreenRendererHandler,
    val screenFactory: (ScreenRendererHandler, PlayerInventory, Text) -> ScreenRenderer<ScreenRendererHandler>
) {
    VINDOR_SCREEN(::ClientVindorScreenHandler, ::VindorClientScreen),
    EVIONE_SCREEN(::EvioneClientScreenHandler, ::EvioneClientScreen),
    WIZARD_SCREEN(::ClientWizardScreenHandler, ::WizardClientScreen);

    private val pathName = name.toLowerCase()
    val translationKey: String = "container.friendlymob.$pathName"
    val screenHandlerType: ScreenHandlerType<ScreenRendererHandler> by lazy { registerScreenHandler() }

    private fun registerScreenHandler(): ScreenHandlerType<ScreenRendererHandler> {
        return register(pathName) { syncId: Int, player: PlayerInventory ->
            handlerFactory(
                screenHandlerType,
                syncId,
                player
            )
        }
    }

    fun registerScreen() {
        ScreenRegistry.register(screenHandlerType) { screenRendererHandler, playerInventory, text ->
            screenFactory(screenRendererHandler, playerInventory, text)
        }
    }

    companion object {

        fun registerScreenHandlerTypes() = values().forEach { Logger.info("Triggered screen handler registration for ${it.pathName}@${it.screenHandlerType}") }

        fun registerClientScreens() = values().forEach { it.registerScreen() }

        private fun <T : ScreenHandler> register(name: String, entry: (Int, PlayerInventory) -> T): ScreenHandlerType<T> {
            return ScreenHandlerRegistry.registerSimple<T>(
                Identifier(BefriendMinecraft.NAMESPACE, name),
                entry
            )
        }
    }
}