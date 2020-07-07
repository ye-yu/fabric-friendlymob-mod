package fp.yeyu.mcvisualmod.events

import net.minecraft.world.World

interface Event {
    fun getProfileName(): String {
        return this.javaClass.simpleName
    }

    fun run(world: World): Boolean
    fun getTickInterval(): Int
}
