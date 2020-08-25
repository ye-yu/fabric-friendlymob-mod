package fp.yeyu.monsterfriend.screens.gui

import fp.yeyu.monsterfriend.screens.wizard.RecipeProvider
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.util.DrawerUtil
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.ceil

class JamcguiWizardRecipeProgressBar(
    relativeX: Int = 0,
    relativeY: Int = 0,
    width: Int = 1,
    height: Int = 1,
    private val growDirection: Direction = Direction.LEFT,
    private val color: Int = DrawerUtil.constructColor(0xFF, 0xFF, 0xFF, 0xFF),
    private val backgroundColor: Int = DrawerUtil.constructColor(0x50, 0x50, 0x50, 0xFF),
    private val slotNumber: Int
) : JamcguiProgressBarWidget(
    relativeX,
    relativeY,
    width,
    height,
    growDirection,
    color,
    backgroundColor,
    "wizard-recipe"
), DynamicPositionChildWidget {
    override var absoluteX: Int = relativeX
    override var absoluteY: Int = relativeY

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        val drawX = super<DynamicPositionChildWidget>.getDrawX()
        val drawY = super<DynamicPositionChildWidget>.getDrawY()

        val handler = screen.getHandler()
        if (handler !is RecipeProvider) return
        val progress = handler.getRecipe(slotNumber).getTickProgress()

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
}