package fp.yeyu.monsterfriend.statics.mutable

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.statics.immutable.ConfigFile
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.PositionTracker
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.*
import java.nio.file.Paths
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.IntStream
import kotlin.math.max

object WonderTrade {
    const val wonderItemTag: String = "wonder_item"
    const val wonderMsgTag = "wonder_msg"
    private const val settingsFile = "wonder.settings.json"
    private var stackLength: Int = ConfigFile.getInt(ConfigFile.Defaults.WONDER_SPACE)
    private var key = AtomicBoolean(false)
    private val logger: Logger = LogManager.getLogger()
    private val random = Random(Instant.now().toEpochMilli())


    init {
        initWonderItems()
    }

    private fun getFile(filename: String): File {
        val parent =
            Paths.get(".").resolve("mods").resolve(BefriendMinecraft::class.simpleName!!).resolve("wonder_trade")
                .toFile()
        if (!parent.exists()) {
            parent.mkdirs()
        }
        return File(parent, filename)
    }

    fun popWonderItem(item: ItemStack, msg: String): WonderProps {
        val slot = Random(Instant.now().toEpochMilli()).nextInt(stackLength)
        logger.info("At $slot:")

        val file = getFile("slot_$slot.dat")
        val tag = CompoundTag.READER.read(DataInputStream(FileInputStream(file)), 0, PositionTracker(Long.MAX_VALUE))
        val wonderProps = WonderProps.fromTag(tag)
        logger.info("Popped ${wonderProps.item.item}")

        if (ConfigFile.getBoolean(ConfigFile.Defaults.SIMULATE_MULTIPLAYER)) {
            val randomItem = randomItemStack()
            val randomMessage = randomMessage()

            writeWonderPropToSlot(
                WonderProps(
                    randomItem,
                    randomMessage
                ), slot
            )
            logger.info("Pushed ${randomItem.item}")
        } else {
            writeWonderPropToSlot(
                WonderProps(
                    item,
                    msg
                ), slot
            )
            logger.info("Pushed ${item.item}")
        }
        return wonderProps
    }

    private fun randomMessage(): String {
        val randomMessages = ConfigFile.getRandomMessages()
        return randomMessages[random.nextInt(randomMessages.size)]
    }

    private fun randomItemStack(): ItemStack {
        val randomItem = Registry.ITEM.getRandom(random)
        val maxCount = max(1, random.nextInt(randomItem.maxCount + 1))
        return ItemStack(randomItem, maxCount)
    }

    fun lock(): Boolean {
        if (key.getAndSet(true)) return false
        return true
    }

    fun unlock() {
        key.set(false)
    }

    private fun initWonderItems() {
        IntStream.range(0, stackLength).forEach(
            WonderTrade::initWonderItem
        )
    }

    private fun initWonderItem(slot: Int) {
        if (getFile("slot_$slot.dat").exists()) return
        val item = randomItemStack()
        val msg = "Congratulation for being the first!"
        println("Initialised ${item.item} at slot $slot")
        writeWonderPropToSlot(
            WonderProps(
                item,
                msg
            ), slot
        )
    }

    private fun writeWonderPropToSlot(wonderProps: WonderProps, slot: Int) {
        if (slot >= stackLength) throw ArrayIndexOutOfBoundsException(slot)
        val file = getFile("slot_$slot.dat")
        wonderProps.toTag().write(DataOutputStream(FileOutputStream(file)))
    }
}

class WonderProps(val item: ItemStack, val msg: String) {
    fun toTag(): CompoundTag {
        val itemTag = CompoundTag()
        val tag = CompoundTag()
        item.toTag(itemTag)
        tag.put(WonderTrade.wonderItemTag, itemTag)
        tag.putString(WonderTrade.wonderMsgTag, msg)
        return tag
    }

    companion object {
        fun fromTag(tag: CompoundTag): WonderProps {
            val item = ItemStack.fromTag(tag.getCompound(WonderTrade.wonderItemTag))
            val msg = tag.getString(WonderTrade.wonderMsgTag)
            return WonderProps(item, msg)
        }
    }
}
