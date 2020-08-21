package fp.yeyu.monsterfriend.screens.wizard

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.provider.IntegerProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack
import java.awt.Rectangle

class JamcguiTwoStatesTexturedScrollBarWidget(
    override val relativeX: Int,
    override val relativeY: Int,
    override val width: Int,
    override val height: Int,
    private val scrollHeight: Int,
    private val texture: TextureDrawerHelper,
    private val inactiveTexture: TextureDrawerHelper,
    // todo: remove bound, listen off focus
    private val relativeBound: Rectangle,
    override val name: String
) : ChildWidget, MouseListener {

    // states
    private var top = true
    private var focused: Boolean = false

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

    override fun isFocused(): Boolean = focused

    override fun isListenOffFocus(): Boolean = false

    override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        focused = isMouseOverBound(mouseX, mouseY)
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        if (isFocused()) top = amount > 0
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

    private fun isMouseOverBound(mouseX: Double, mouseY: Double): Boolean {
        val absX: Int = getParent().getDrawX() + relativeX + relativeBound.x
        val absY: Int = getParent().getDrawY() + relativeY + relativeBound.y
        return (betweenIncExc(absX, mouseX.toInt(), absX + relativeBound.width)
                && betweenIncExc(absY, mouseY.toInt(), absY + relativeBound.height))
    }
}