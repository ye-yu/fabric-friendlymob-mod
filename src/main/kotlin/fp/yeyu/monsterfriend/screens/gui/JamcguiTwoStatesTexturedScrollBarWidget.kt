package fp.yeyu.monsterfriend.screens.gui

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.provider.IntegerProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack

class JamcguiTwoStatesTexturedScrollBarWidget(
    override val relativeX: Int,
    override val relativeY: Int,
    override val width: Int,
    override val height: Int,
    private val scrollHeight: Int,
    private val texture: TextureDrawerHelper,
    private val inactiveTexture: TextureDrawerHelper,
    override val name: String
) : ChildWidget, MouseListener {

    // states
    private var top = true

    private lateinit var parent: ParentWidget
    var scrollablePredicate: (Int) -> Boolean = { true }

    override fun getParent(): ParentWidget = parent

    override fun setParent(parent: ParentWidget) {
        this.parent = parent
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        val drawX = getDrawX()
        val drawY = getDrawY()
        if (scrollablePredicate((screen.getHandler() as IntegerProvider).getInteger(name)))
            texture.drawOn(matrices, drawX, drawY + if (top) 0 else scrollHeight - height)
        else
            inactiveTexture.drawOn(matrices, drawX, drawY)
    }

    override fun isFocused(): Boolean = isListenOffFocus()

    override fun isListenOffFocus(): Boolean = true

    override fun setFocused(focused: Boolean) {
    }

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        top = amount > 0
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

}