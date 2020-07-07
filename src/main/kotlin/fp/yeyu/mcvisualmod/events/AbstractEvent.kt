package fp.yeyu.mcvisualmod.events

import net.minecraft.world.World

abstract class AbstractEvent : Event {
    private var tick = 0

    override fun run(world: World): Boolean {
        tick %= getTickInterval()
        return tick++ == 0
    }

    companion object {
        private const val MAX_TPS = 20
        fun convertSecondsToTick(seconds: Int): Int {
            return seconds * MAX_TPS
        }
    }
}