package fp.yeyu.monsterfriend.screens.gui

import com.mojang.blaze3d.systems.RenderSystem
import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.screens.wizard.RecipeClickListener
import fp.yeyu.monsterfriend.screens.wizard.RecipeProvider
import io.github.yeyu.gui.handler.ScreenRendererHandler
import io.github.yeyu.gui.renderer.ScreenRenderer
import io.github.yeyu.gui.renderer.widget.ChildWidget
import io.github.yeyu.gui.renderer.widget.ParentWidget
import io.github.yeyu.gui.renderer.widget.listener.MouseListener
import io.github.yeyu.util.TextureDrawerHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier

class JamcguiSingleRecipeButton(
    private val slot: Int,
    override val name: String
) : ChildWidget, MouseListener {

    private var focused = false
    var hovered = false
    override val relativeX: Int = 0
    override val relativeY: Int = 0
    override val width: Int = 88
    override val height: Int = 47

    var offsetY = 0

    internal object Drawer {
        val background = TextureDrawerHelper(
            Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/wizard/button.png"),
            0,
            0,
            88,
            47,
            0,
            0,
            176,
            47
        )

        val hoveredBackground = TextureDrawerHelper(
            Identifier(BefriendMinecraft.NAMESPACE, "textures/gui/wizard/button.png"),
            88,
            0,
            88,
            47,
            0,
            0,
            176,
            47
        )

        const val marginY = 4
        const val marginX = 5
    }

    private lateinit var parent: ParentWidget
    private var tooltipItem = ItemStack.EMPTY

    override fun getParent(): ParentWidget = parent

    override fun setParent(parent: ParentWidget) {
        this.parent = parent
    }

    override fun render(matrices: MatrixStack, relativeMouseX: Int, relativeMouseY: Int, screen: ScreenRenderer<*>) {
        val (toCraft, item1, item2, flower, potion, _) = (screen.getHandler() as RecipeProvider).getRecipe(slot)
        if (toCraft.isEmpty) return

        val drawX = getDrawX()
        val drawY = getDrawY() + offsetY
        if (hovered) {
            Drawer.hoveredBackground.drawOn(matrices, drawX, drawY)
        } else {
            Drawer.background.drawOn(matrices, drawX, drawY)
        }

        if (hovered) {
            screen.setItemToRenderTooltip(tooltipItem)
        }

        drawItemStack(item1, drawX + Drawer.marginX, drawY + Drawer.marginY)
        drawItemStack(item2, drawX + Drawer.marginX + 25, drawY + Drawer.marginY)
        drawItemStack(toCraft, drawX + Drawer.marginX + 61, drawY + Drawer.marginY + 10)
        drawItemStack(flower, drawX + Drawer.marginX, drawY + Drawer.marginY + 18)
        drawItemStack(potion, drawX + Drawer.marginX + 25, drawY + Drawer.marginY + 18)
    }

    override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    override fun isFocused(): Boolean = focused

    override fun isListenOffFocus(): Boolean = true

    override fun <T : ScreenRendererHandler> onMouseDown(mouseX: Double, mouseY: Double, button: Int, handler: T) {
        if (!isMouseOver(mouseX, mouseY) || !focused) return
        if ((handler as RecipeProvider).getRecipe(slot).toCraft.isEmpty) return
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f))
        (handler as RecipeClickListener).onRecipeButtonClick(slot)
    }

    override fun <T : ScreenRendererHandler> onMouseMove(mouseX: Double, mouseY: Double, handler: T) {
        hovered = isMouseOver(mouseX, mouseY)
        val (toCraft, item1, item2, flower, potion, _) = (handler as RecipeProvider).getRecipe(slot)
        if (toCraft.isEmpty) return

        val hoverOffsetX = 2
        val hoverOffsetY = 6 + offsetY

        tooltipItem = when {
            isMouseOverItemBound(mouseX, mouseY, Drawer.marginX + hoverOffsetX, Drawer.marginY + hoverOffsetY) -> item1
            isMouseOverItemBound(
                mouseX,
                mouseY,
                Drawer.marginX + hoverOffsetX + 25,
                Drawer.marginY + hoverOffsetY
            ) -> item2
            isMouseOverItemBound(
                mouseX,
                mouseY,
                Drawer.marginX + hoverOffsetX + 61,
                Drawer.marginY + hoverOffsetY + 7
            ) -> toCraft
            isMouseOverItemBound(
                mouseX,
                mouseY,
                Drawer.marginX + hoverOffsetX,
                Drawer.marginY + hoverOffsetY + 16
            ) -> flower
            isMouseOverItemBound(
                mouseX,
                mouseY,
                Drawer.marginX + hoverOffsetX + 25,
                Drawer.marginY + hoverOffsetY + 16
            ) -> potion
            else -> ItemStack.EMPTY
        }

    }

    override fun <T : ScreenRendererHandler> onMouseOver(mouseX: Int, mouseY: Int, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseScroll(mouseX: Double, mouseY: Double, amount: Double, handler: T) {
    }

    override fun <T : ScreenRendererHandler> onMouseUp(mouseX: Double, mouseY: Double, button: Int, handler: T) {
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return super.isMouseOver(mouseX, mouseY - offsetY)
    }

    private fun drawItemStack(stack: ItemStack, drawX: Int, drawY: Int) {
        RenderSystem.enableDepthTest()
        val mc = MinecraftClient.getInstance()
        val renderer = mc.itemRenderer
        renderer.zOffset = 100f
        renderer.renderInGuiWithOverrides(stack, drawX + 1, drawY + 1)
        renderer.renderGuiItemOverlay(mc.textRenderer, stack, drawX + 1, drawY + 1)
        renderer.zOffset = 0f
    }

    private fun isMouseOverItemBound(mouseX: Double, mouseY: Double, offsetX: Int, offsetY: Int): Boolean {
        val absX: Int = getParent().getDrawX() + relativeX + offsetX
        val absY: Int = getParent().getDrawY() + relativeY + offsetY
        return (betweenIncExc(absX, mouseX.toInt(), absX + 16)
                && betweenIncExc(absY, mouseY.toInt(), absY + 16))
    }
}