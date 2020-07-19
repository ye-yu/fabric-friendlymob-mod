package fp.yeyu.monsterfriend.screens.widget

import io.github.cottonmc.cotton.gui.widget.data.Color
import java.lang.NumberFormatException
import kotlin.math.floor

object ColorUtils {
    fun decomposeColor(color: Int): Array<Int> {
        val rgb = Color.RGB(color)
        return arrayOf(rgb.r, rgb.g, rgb.b, rgb.a)
    }

    fun decomposeColor(color: Long): Array<Int> {
        val rgb = Color.RGB(color.toInt())
        return arrayOf(rgb.r, rgb.g, rgb.b, rgb.a)
    }

    fun composeColor(r: Int, g: Int, b: Int, a: Int): Int {
        assertUByte(r)
        assertUByte(g)
        assertUByte(b)
        assertUByte(a)
        return Color.RGB(a, r, g, b).toRgb()
    }

    private fun assertUByte(i: Int) {
        if (i < 0x0) throw NumberFormatException("$i is out of bound 0 <= x <= ${0xFF}")
        if (i > 0xFF) throw NumberFormatException("$i is out of bound 0 <= x <= ${0xFF}")
    }

    fun createLinearInterpolator(minFrom: Double, maxFrom: Double, minTo: Double, maxTo: Double): LinearInterpolator {
        return LinearInterpolator(minFrom, maxFrom, minTo, maxTo)
    }

    fun createLinearInterpolator(minFrom: Number, maxFrom: Number, minTo: Number, maxTo: Number): LinearInterpolator {
        return createLinearInterpolator(minFrom.toDouble(), maxFrom.toDouble(), minTo.toDouble(), maxTo.toDouble())
    }

    fun createColorInterpolator(from: Int, to: Int): ColorInterpolator {
        return ColorInterpolator(from, to)
    }

    fun niceRgb(color: Int): String {
        val colorChannels = decomposeColor(color)
        return "R:${colorChannels[0]} G:${colorChannels[1]} B:${colorChannels[2]}"
    }

    class LinearInterpolator(private val x1: Double, private val y1: Double, private val x2: Double, private val y2: Double) {
        fun extrapolate(p: Double): Double {
            return normalise(p, x1, y1) * (y2 - x2) + x2
        }

        private fun normalise(p: Double, min: Double, max: Double): Double {
            return (p - min) / (max - min)
        }
    }

    class ColorInterpolator(colorFrom: Int, colorTo: Int) {
        private val colorFromDecomposed = decomposeColor(colorFrom)
        private val colorToDecomposed = decomposeColor(colorTo)

        private val rInterpolator = createLinearInterpolator(0, 1, colorFromDecomposed[0], colorToDecomposed[0])
        private val gInterpolator = createLinearInterpolator(0, 1, colorFromDecomposed[1], colorToDecomposed[1])
        private val bInterpolator = createLinearInterpolator(0, 1, colorFromDecomposed[2], colorToDecomposed[2])

        fun interpolate(point: Double): Int {
            if (point < 0) return 0
            if (point > 1) return composeColor(0, 0, 0, 0xFF)
            return composeColor(
                floor(rInterpolator.extrapolate(point)).toInt(),
                floor(gInterpolator.extrapolate(point)).toInt(),
                floor(bInterpolator.extrapolate(point)).toInt(),
                0xFF
            )
        }
    }
}
