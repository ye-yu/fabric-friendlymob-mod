package fp.yeyu.mcvisualmod.screens

import com.google.common.collect.Maps
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import java.lang.IllegalStateException

object Magic {
    private val MAP: HashMap<Key, SyncedGuiDescription> = Maps.newHashMap()

    enum class Key {
        OPEN_SCREEN
    }

    fun push(key: Key, obj: SyncedGuiDescription) {
        if (MAP.containsKey(key)) throw IllegalStateException("Key already exist.")
        MAP[key] = obj
    }

    fun pop(key: Key): SyncedGuiDescription? {
        return MAP.remove(key)
    }

}
