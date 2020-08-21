package fp.yeyu.monsterfriend.screens.gui

import io.github.yeyu.gui.handler.provider.BooleanProvider
import io.github.yeyu.gui.handler.provider.DoubleProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.ceil

class JamcguiTexturedProgressBarWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    override val width: Int = 1,
    override val height: Int = 1,
    private val growDirection: Direction = Direction.LEFT,
    private val activeTexture: TextureDrawerHelper,
    private val bgTexture: TextureDrawerHelper,
    override val name: String
) : ChildWidget {

    private var parent: ParentWidget? = null

    override fun getParent(): ParentWidget? {
        return parent
    }

    override fun setParent(parent: ParentWidget) {
        this.parent = parent
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        if (!(screen.getHandler() as BooleanProvider).getBoolean(name)) return
        val drawX = getDrawX()
        val drawY = getDrawY()

        val handler = screen.getHandler()
        if (handler !is DoubleProvider) return
        val progress = handler.getDouble(name)

        bgTexture.drawOn(matrices, drawX, drawY)
        when (growDirection) {
            Direction.UP -> {
                val progressHeight = ceil(progress * height).toInt().coerceAtMost(height)
                // todo: start from the middle
                activeTexture.drawOn(matrices, drawX, drawY + height - progressHeight, width, progressHeight)
            }

            Direction.DOWN -> {
                val progressHeight = ceil(progress * height).toInt().coerceAtMost(height)
                activeTexture.drawOn(matrices, drawX, drawY + progressHeight, width, height - progressHeight)
            }

            Direction.LEFT -> {
                val progressWidth = ceil(progress * width).toInt().coerceAtMost(width)
                activeTexture.drawOn(matrices, drawX, drawY, progressWidth, height)
            }

            Direction.RIGHT -> {
                val progressWidth = ceil(progress * width).toInt().coerceAtMost(width)
                // todo: start from the middle
                activeTexture.drawOn(matrices, drawX + width - progressWidth, drawY, progressWidth, height)
            }
        }
    }

    override fun setFocused(focused: Boolean) {
        // does nothing
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }
}