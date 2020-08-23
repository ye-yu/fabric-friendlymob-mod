package fp.yeyu.monsterfriend.screens.gui

import io.github.yeyu.gui.renderer.widget.ChildWidget

interface DynamicPositionChildWidget : ChildWidget {
    var absoluteX: Int
    var absoluteY: Int

    fun withPosition(x: Int, y: Int): DynamicPositionChildWidget {
        this.absoluteX = x
        this.absoluteY = y
        return this
    }

    override fun getDrawX(): Int = absoluteX
    override fun getDrawY(): Int = absoluteY
}