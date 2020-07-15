package fp.yeyu.mcvisualmod.mobs.entity

import com.google.common.collect.Maps
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.PositionTracker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.*
import java.time.Instant
import java.util.*
import kotlin.math.max

class VindorUtils {
    object INSTANCE {
        val util = VindorUtils()
    }

    private val wonderItemTag: String = "wonder_item"
    private val wonderMsgTag = "wonder_msg"
    val scheduleFile = "wonder.schedule"
    var key = false
    private val logger: Logger = LogManager.getLogger()

    fun popWonderItem(world: ServerWorld, item: ItemStack, msg: String): WonderProps {
        val file = world.server.getFile(scheduleFile)
        createIfNotExists(world, scheduleFile)
        val tag = CompoundTag().reader.read(DataInputStream(FileInputStream(file)), 0, PositionTracker(Long.MAX_VALUE))

        val itemStack = ItemStack.fromTag(tag.getCompound(wonderItemTag))
        val message = tag.getString(wonderMsgTag)
        saveWonderProp(WonderProps(item, msg), file)

        logger.info("Popped ${itemStack.item}")
        logger.info("Pushed ${item.item}")
        return WonderProps(itemStack, message)
    }

    fun saveWonderProp(wonderProps: WonderProps, file: File) {
        val item = wonderProps.item
        val msg = wonderProps.msg
        val itemTag = CompoundTag()
        val wonderTag = CompoundTag()
        item.toTag(itemTag)
        wonderTag.put(wonderItemTag, itemTag)
        wonderTag.putString(wonderMsgTag, msg)
        wonderTag.write(DataOutputStream(FileOutputStream(file)))

    }

    fun hasWonderItem(world: ServerWorld): Boolean {
        return !createIfNotExists(world, scheduleFile)
    }

    fun lock(): Boolean {
        if (key) return false
        key = true
        return true
    }

    fun unlock() {
        key = false
    }

    fun createIfNotExists(world: ServerWorld, filename: String): Boolean {
        val file = world.server.getFile(filename)
        if (!file.exists()) {
            if (file.createNewFile()) {
                logger.info(String.format("Created wonder file at: %s", file.absolutePath))
                val random = Random(Instant.now().toEpochMilli())
                val randomItem = Registry.ITEM.getRandom(random)
                val maxCount = max(1, random.nextInt(randomItem.maxCount + 1))
                val item = ItemStack(randomItem, maxCount)
                val msg = "Congratulation for being the first!"
                saveWonderProp(WonderProps(item, msg), file)
                return true
            }
        }
        return false
    }

    object VindorInteraction {
        private val INTERACTIONS = Maps.newHashMap<ServerPlayerEntity, Vindor>()

        @JvmStatic
        fun push(player: ServerPlayerEntity, vindor: Vindor) {
            INTERACTIONS[player] = vindor
        }

        fun pop(player: ServerPlayerEntity): Vindor {
            return INTERACTIONS.remove(player)!!
        }
    }
}


class WonderProps(val item: ItemStack, val msg: String)

