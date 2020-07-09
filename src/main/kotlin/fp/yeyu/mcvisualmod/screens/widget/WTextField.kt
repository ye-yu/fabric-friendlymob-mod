package fp.yeyu.mcvisualmod.screens.widget

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import io.github.cottonmc.cotton.gui.widget.WTextField as WTextFieldLibGui

class WTextField : WTextFieldLibGui() {
    @Environment(EnvType.CLIENT)
    override fun onKeyPressed(ch: Int, key: Int, modifiers: Int) {
        if (!this.isFocused) return
        if (Screen.isSelectAll(ch)) {
            this.setCursorPos(this.text.length)
            this.select = -cursor
            return
        }

        if (Screen.isCopy(ch)) {
            MinecraftClient.getInstance().keyboard.clipboard = selection
            return
        }

        if (Screen.isPaste(ch)) {
            insertText(MinecraftClient.getInstance().keyboard.clipboard)
        }

        if (Screen.isCut(ch)) {
            MinecraftClient.getInstance().keyboard.clipboard = selection
            eraseFromCursor(false)
        }

        when (ch) {
            GLFW.GLFW_KEY_BACKSPACE -> {
                eraseFromCursor(false)
                return
            }
            GLFW.GLFW_KEY_DELETE -> {
                eraseFromCursor(true)
                return
            }
            GLFW.GLFW_KEY_LEFT -> {
                if (cursorAt == CursorAt.RIGHT) {
                    cursorAt = CursorAt.MIDDLE
                }
                if (Screen.hasShiftDown()) {
                    select -= 1
                } else {
                    if (select != 0) {
                        cursor += select
                    }
                    cursor--
                    select = 0
                }
                validateCursors()
                return
            }
            GLFW.GLFW_KEY_RIGHT -> {
                if (cursorAt == CursorAt.LEFT) {
                    cursorAt = CursorAt.MIDDLE
                }
                if (Screen.hasShiftDown()) {
                    select += 1
                } else {
                    if (0 != select) {
                        cursor += select
                    }
                    cursor++
                    select = 0
                }
                validateCursors()
                return
            }
            GLFW.GLFW_KEY_HOME -> {
                if (Screen.hasShiftDown()) {
                    select = -cursor
                } else {
                    cursor = 0
                }
                validateCursors()
            }
            GLFW.GLFW_KEY_END -> {
                if (Screen.hasShiftDown()) {
                    select = text.length - cursor
                } else {
                    cursor = text.length
                }
                validateCursors()
            }
        }
    }

    override fun onCharTyped(ch: Char) {
        if (!editable) return
        if (text.length >= maxLength) return
        validateCursors()
        val curLeft: Int = min(cursor + select, cursor)
        val curRight: Int = max(cursor + select, cursor)
        this.text = this.text.substring(0, curLeft) + ch + this.text.substring(curRight, this.text.length)
        cursor++
        select = 0
    }

    private fun eraseFromCursor(forward: Boolean) {
        if (!editable) return
        if (0 == select) {
            if (forward) {
                select += 1
            } else {
                select -= 1
            }
        }
        validateCursors()
        val curLeft: Int = min(cursor + select, cursor)
        val curRight: Int = max(cursor + select, cursor)
        this.text = this.text.substring(0, curLeft) + this.text.substring(curRight, this.text.length)
        setCursorPos(curLeft)
        select = 0
    }

    private fun insertText(clipboard: String) {
        clipboard.toCharArray().forEach {
            onCharTyped(it)
        }
    }

    private fun validateCursors() {
        cursor = min(text.length, cursor)
        cursor = max(0, cursor)
        select = min(text.length - cursor, select) // text.length - cursor: maximum offset before out of bound
        select = max(-cursor, select) // - cursor: minimum offset before out of bound
        setCursorPos(cursor)
    }

    override fun getSelection(): String? {
        validateCursors()
        val a = min(cursor + select, cursor)
        val b = max(cursor + select, cursor)
        return text.substring(a, b)
    }

    private var fieldWidth = width
    fun setWidth(width: Int) {
        fieldWidth = width
    }

    override fun getWidth(): Int {
        return fieldWidth
    }

    enum class CursorAt {
        LEFT, MIDDLE, RIGHT
    }

    private var cursorAt = CursorAt.RIGHT
    private var caretTimeout = 0
    private var blink = false

    @Environment(EnvType.CLIENT)
    override fun renderTextField(matrices: MatrixStack?, x: Int, y: Int) {
        validateCursors()
        val font = MinecraftClient.getInstance().textRenderer
        val borderColor = if (this.isFocused) -0x60 else -0x5f5f60
        ScreenDrawing.coloredRect(x - 1, y - 1, width + 2, height + 2, borderColor)
        ScreenDrawing.coloredRect(x, y, width, height, -0x1000000)
        val textColor = if (editable) enabledColor else uneditableColor
        val textY = (y + (height - 8) / 2)

        val precursorCandidate = text.substring(0, cursor + select)
        val precursorTrimmed = font.trimToWidth(precursorCandidate, fieldWidth, true)
        val precursorWidth = font.getWidth(precursorTrimmed)

        val postcursorCandidate = text.substring(cursor + select)
        val postcursorTrimmed = font.trimToWidth(postcursorCandidate, max(fieldWidth - precursorWidth, 0))
        font.drawWithShadow(matrices, precursorTrimmed, (x + OFFSET_X_TEXT).toFloat(), textY.toFloat(), textColor)
        font.drawWithShadow(matrices, postcursorTrimmed, (x + precursorWidth + OFFSET_X_TEXT).toFloat(), textY.toFloat(), textColor)

        if (isFocused && blink)
            ScreenDrawing.coloredRect(x + precursorWidth + OFFSET_X_TEXT, textY - 2, 1, 12, -0x2f2f30)

        caretTimeout = (++caretTimeout) % CARET_MAX_TIMEOUT
        if (caretTimeout == 0) {
            blink = !blink
        }

        if (select == 0) return

        if (select < 0) {
            val hl = postcursorTrimmed.substring(0, min(postcursorTrimmed.length, abs(select)))
            val hlWidth = font.getWidth(hl)
            invertedRect(x + OFFSET_X_TEXT + precursorWidth, textY - 2, hlWidth, font.fontHeight + 3)
        } else {
            val hl = precursorTrimmed.substring(max(precursorTrimmed.length - abs(select), 0))
            val hlWidth = font.getWidth(hl)
            invertedRect(x + OFFSET_X_TEXT + precursorWidth - hlWidth, textY - 2, hlWidth, font.fontHeight + 3)
        }
    }


    @Environment(EnvType.CLIENT)
    private fun invertedRect(x: Int, y: Int, width: Int, height: Int) {
        val tessellator1 = Tessellator.getInstance()
        val bufferbuilder1 = tessellator1.buffer
        @Suppress("DEPRECATION")
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f)
        RenderSystem.disableTexture()
        RenderSystem.enableColorLogicOp()
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE)
        bufferbuilder1.begin(GL11.GL_QUADS, VertexFormats.POSITION)
        bufferbuilder1.vertex(x.toDouble(), y + height.toDouble(), 0.0).next()
        bufferbuilder1.vertex(x + width.toDouble(), y + height.toDouble(), 0.0).next()
        bufferbuilder1.vertex(x + width.toDouble(), y.toDouble(), 0.0).next()
        bufferbuilder1.vertex(x.toDouble(), y.toDouble(), 0.0).next()
        tessellator1.draw()
        RenderSystem.disableColorLogicOp()
        RenderSystem.enableTexture()
    }

    companion object {
        private const val CARET_MAX_TIMEOUT = 15
    }
}