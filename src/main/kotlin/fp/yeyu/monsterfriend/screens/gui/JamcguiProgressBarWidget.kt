package fp.yeyu.monsterfriend.screens.gui

import io.github.yeyu.gui.handler.provider.DoubleProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.ceil

class JamcguiProgressBarWidget(
    override val relativeX: Int = 0,
    override val relativeY: Int = 0,
    override val width: Int = 1,
    override val height: Int = 1,
    private val growDirection: Direction = Direction.LEFT,
    private val color: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val backgroundColor: Int = DrawerUtil.constructColor(0x50, 0x50, 0x50, 0xFF),
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
        val drawX = getDrawX()
        val drawY = getDrawY()

        val handler = screen.getHandler()
        if (handler !is DoubleProvider) return
        val progress = handler.getDouble(name)

        DrawerUtil.coloredRect(drawX, drawY, width, height, backgroundColor)
        when (growDirection) {
            Direction.UP -> {
                val progressHeight = ceil(progress * height).toInt().coerceAtMost(height)
                DrawerUtil.coloredRect(drawX, drawY + height - progressHeight, width, progressHeight, color)
            }

            Direction.DOWN -> {
                val progressHeight = ceil(progress * height).toInt().coerceAtMost(height)
                DrawerUtil.coloredRect(drawX, drawY + progressHeight, width, height - progressHeight, backgroundColor)
            }

            Direction.LEFT -> {
                val progressWidth = ceil(progress * width).toInt().coerceAtMost(width)
                DrawerUtil.coloredRect(drawX, drawY, progressWidth, height, color)
            }

            Direction.RIGHT -> {
                val progressWidth = ceil(progress * width).toInt().coerceAtMost(width)
                DrawerUtil.coloredRect(drawX + width - progressWidth, drawY, progressWidth, height, color)
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