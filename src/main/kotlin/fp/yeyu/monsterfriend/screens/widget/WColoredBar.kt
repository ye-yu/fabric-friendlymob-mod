package fp.yeyu.monsterfriend.screens.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.util.math.MatrixStack
import java.time.Instant
import kotlin.math.floor
import kotlin.random.Random

class WColoredBar private constructor(
    private val direction: WBar.Direction,
    private val barWidth: Int,
    private val barHeight: Int,
    private val maxValue: Int,
    lowestColor: Int,
    highestColor: Int,
    private val backgroundColor: Int,
    private val offsetX: Int,
    private val offsetY: Int
) : WWidget() {

    override fun getWidth(): Int {
        return barWidth
    }

    override fun getHeight(): Int {
        return barHeight
    }

    fun setProgress(p: Int) {
        value = when {
            p > maxValue -> maxValue
            p < 0 -> 0
            else -> p
        }
    }

    fun setProgress(percentage: Double) {
        value = when {
            percentage > 1 -> maxValue
            percentage < 0 -> 0
            else -> floor(percentage * maxValue).toInt()
        }
    }

    private var debug = false
    private var debugTick = -1
    private var random = Random(Instant.now().toEpochMilli())

    fun setDebug(d: Boolean) {
        debug = d
        if (d) debugTick = 20
    }

    private var value: Int = 0
    private val colorInterpolator = ColorUtils.createColorInterpolator(lowestColor, highestColor)
    private val lengthInterpolator =
        ColorUtils.createLinearInterpolator(
            0,
            maxValue,
            0,
            if (direction == WBar.Direction.UP || direction == WBar.Direction.DOWN) barHeight else barWidth
        )

    override fun paint(matrices: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        paintWithOffset(x + offsetX, y + offsetY)
    }

    private fun paintWithOffset(x: Int, y: Int) {
        ScreenDrawing.coloredRect(x, y, barWidth, barHeight, backgroundColor)

        val interpolation = value / maxValue.toDouble()
        val progressColor = colorInterpolator.interpolate(interpolation)
        val progressLength = lengthInterpolator.extrapolate(value.toDouble())

        when (direction) {
            WBar.Direction.UP -> ScreenDrawing.coloredRect(
                x,
                y + barHeight - floor(progressLength).toInt(),
                barWidth,
                floor(progressLength).toInt(),
                progressColor
            )
            WBar.Direction.DOWN -> ScreenDrawing.coloredRect(
                x,
                y,
                barWidth,
                floor(progressLength).toInt(),
                progressColor
            )
            WBar.Direction.LEFT -> ScreenDrawing.coloredRect(
                x,
                y,
                floor(progressLength).toInt(),
                barHeight,
                progressColor
            )
            WBar.Direction.RIGHT -> ScreenDrawing.coloredRect(
                x + barWidth - floor(progressLength).toInt(),
                y,
                floor(progressLength).toInt(),
                barHeight,
                progressColor
            )
        }

        if (debug) {
            debugTick = ++debugTick % 20
            if (debugTick == 0) {
                setProgress(random.nextDouble())
            }
        }
    }

    class Builder(private val maxValue: Int, private val barWidth: Int, private val barHeight: Int) {
        private var offsetX: Int = 0
        private var offsetY: Int = 0
        private var axis = WBar.Direction.UP
        private var lowestColor: Int = ColorUtils.composeColor(0xFF, 0x55, 0x55, 0xFF)
        private var highestColor: Int = ColorUtils.composeColor(0x55, 0xFF, 0x55, 0xFF)
        private var backgroundColor: Int = ColorUtils.composeColor(0x55, 0x55, 0x55, 0xFF)

        fun setDirection(axis: WBar.Direction): Builder {
            this.axis = axis
            return this
        }

        fun setLowestColor(color: Int): Builder {
            this.lowestColor = color
            return this
        }

        fun setHighestColor(color: Int): Builder {
            this.highestColor = color
            return this
        }

        fun setBackgroundColor(color: Int): Builder {
            backgroundColor = color
            return this
        }

        fun build(): WColoredBar {
            return WColoredBar(
                axis,
                barWidth,
                barHeight,
                maxValue,
                lowestColor,
                highestColor,
                backgroundColor,
                offsetX,
                offsetY
            )
        }

        fun gridOffset(x: Int, y: Int): Builder {
            offsetX = x
            offsetY = y
            return this
        }
    }
}