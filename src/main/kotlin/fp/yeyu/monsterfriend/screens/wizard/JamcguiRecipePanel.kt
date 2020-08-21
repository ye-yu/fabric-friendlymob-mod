package fp.yeyu.monsterfriend.screens.wizard

import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.handler.provider.IntegerProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import net.minecraft.client.util.math.MatrixStack

class JamcguiRecipePanel(
    override val relativeX: Int,
    override val relativeY: Int,
    override val horizontalAnchor: ParentWidget.AnchorType,
    override val verticalAnchor: ParentWidget.AnchorType,
    override val name: String
) : ParentWidget, MouseListener {

    override var parentScreen: ScreenRenderer<*>? = null

    override val height: Int = 140
    override val width: Int = 87
    private val children: List<JamcguiSingleRecipeButton>
    private var scrollAt = 0

    init {
        children = List(5, this::createChildren)
        validateChildren()
    }

    private fun createChildren(index: Int): JamcguiSingleRecipeButton {
        return JamcguiSingleRecipeButton(index, "recipe-slot-$index").apply { setParent(this@JamcguiRecipePanel) }
    }

    override fun add(w: ChildWidget) {
        throw IllegalAccessException("Recipe Panel does not accept child widgets!")
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        children[scrollAt].render(matrices, relativeMouseX, relativeMouseY, screen)
        children[scrollAt + 1].render(matrices, relativeMouseX, relativeMouseY, screen)
        if (scrollAt < 3) children[scrollAt + 2].render(matrices, relativeMouseX, relativeMouseY, screen)
    }

    override fun isFocused(): Boolean = isListenOffFocus()

    override fun isListenOffFocus(): Boolean = true

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        children.forEach { it.onMouseDown(mouseX, mouseY, button, handler) }
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        children.forEach { it.onMouseMove(mouseX, mouseY, handler) }
    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
        val level = (handler as IntegerProvider).getInteger(WizardPackets.LEVEL) - 1
        if (level < 3) return
        if (amount < 0) {
            scrollAt++
            if (level == 4) scrollAt++
        } else {
            scrollAt--
            if (level == 4) scrollAt--
        }

        scrollAt = scrollAt.coerceAtLeast(0)
            .coerceAtMost(2)
        if (level == 3) scrollAt = scrollAt.coerceAtMost(1)

        validateChildren()
    }

    private fun validateChildren() {
        children.forEach {
            it.setFocused(false)
            it.hovered = false
        }
        children[scrollAt].apply {
            offsetY = 0
            setFocused(true)
        }
        children[scrollAt + 1].apply {
            offsetY = 47
            setFocused(true)
        }
        if (scrollAt < 3) {
            children[scrollAt + 2].apply {
                offsetY = 94
                setFocused(true)
            }
        }
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

    override fun setFocused(focused: Boolean) {
    }
}