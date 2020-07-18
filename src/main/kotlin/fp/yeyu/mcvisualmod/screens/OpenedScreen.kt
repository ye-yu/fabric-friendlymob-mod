package fp.yeyu.mcvisualmod.screens

import io.github.cottonmc.cotton.gui.SyncedGuiDescription

object OpenedScreen {
    private var screen: SyncedGuiDescription? = null

    fun set(obj: SyncedGuiDescription) {
        if (screen != null) throw IllegalStateException("Screen already exist!")
        screen = obj
    }

    fun unset(): SyncedGuiDescription {
        if (screen == null) throw NullPointerException("Screen does not exist!")
        val r = screen!!
        screen = null
        return r
    }
}
