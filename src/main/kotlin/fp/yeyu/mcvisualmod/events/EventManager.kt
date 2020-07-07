package fp.yeyu.mcvisualmod.events

import com.google.common.collect.Maps
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class EventManager {
    companion object {
        private val WORLD_EVENTS = Maps.newHashMap<World, MutableList<Event>>()
        private val EVENTS = mutableListOf<KClass<out Event>>()
        private val LOGGER: Logger = LogManager.getLogger()

        fun getEvents(world: World): MutableList<Event>? {
            if (!WORLD_EVENTS.containsKey(world)) reloadEvents(world)
            return WORLD_EVENTS[world]
        }

        fun reloadEvents(world: World) {
            val events = mutableListOf<Event>()
            EVENTS.forEach {
                events += it.createInstance()
            }
            WORLD_EVENTS[world] = events
            LOGGER.info(String.format("Reloaded %d events for world %s.", EVENTS.size, world))
        }

        fun addEvent(e: KClass<out Event>) {
            EVENTS.add(e)
            LOGGER.info(String.format("Registered %s event.", e.simpleName))
        }
    }
}