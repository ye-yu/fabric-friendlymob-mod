package fp.yeyu.mcvisualmod.mobs.entity

import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fp.yeyu.mcvisualmod.SilentMinecraft
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

object VindorUtils {
    const val wonderItemTag: String = "wonder_item"
    const val wonderMsgTag = "wonder_msg"
    private const val scheduleFile = "wonder.schedule"
    private const val settingsFile = "wonder.settings.json"
    private var stackLength = 0
    private var key = AtomicBoolean(false)
    private val logger: Logger = LogManager.getLogger()
    private val random = Random(Instant.now().toEpochMilli())


    init {
        createIfNotExists()
    }

    fun forceInvoke(@Suppress("UNUSED_PARAMETER") silentMinecraft: SilentMinecraft) {}

    private fun getFile(filename: String): File {
        val parent = Paths.get(".").resolve("mods").resolve(SilentMinecraft::class.simpleName!!).toFile()
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

        writeWonderPropToSlot(WonderProps(item, msg), slot)
        logger.info("Pushed ${item.item}")
        return wonderProps
    }

    fun lock(): Boolean {
        if (key.getAndSet(true)) return false
        return true
    }

    fun unlock() {
        key.set(false)
    }

    private fun createIfNotExists(): Boolean {
        val file = getFile(scheduleFile)
        if (!file.exists()) {
            initSettings()
            initWonderItems()
            return true
        }
        return false
    }

    private fun initWonderItems() {
        val wonderSettingFile = getFile(settingsFile)
        val parse = JsonParser().parse(JsonReader(FileReader(wonderSettingFile))).asJsonObject
        stackLength = parse["space"].asInt

        IntStream.range(0, stackLength).forEach(::initWonderItem)
    }

    private fun initWonderItem(slot: Int) {
        if (getFile("slot_$slot.dat").exists()) return
        val randomItem = Registry.ITEM.getRandom(random)
        val maxCount = max(1, random.nextInt(randomItem.maxCount + 1))
        val item = ItemStack(randomItem, maxCount)
        val msg = "Congratulation for being the first!"
        println("Initialised $randomItem at slot $slot")
        writeWonderPropToSlot(WonderProps(item, msg), slot)
    }

    private fun writeWonderPropToSlot(wonderProps: WonderProps, slot: Int) {
        if (slot >= stackLength) throw ArrayIndexOutOfBoundsException(slot)
        val file = getFile("slot_$slot.dat")
        wonderProps.toTag().write(DataOutputStream(FileOutputStream(file)))
    }

    private fun initSettings() {
        val wonderSettingFile = getFile(settingsFile)
        if (wonderSettingFile.exists()) return
        val json = JsonWriter(FileWriter(wonderSettingFile))
        json.setIndent("  ")
        json.beginObject()
            .name("space").value(8)
            .name("simulateMultiplayer").value(true)
            .endObject().close()
    }
}

class WonderProps(val item: ItemStack, val msg: String) {
    fun toTag(): CompoundTag {
        val itemTag = CompoundTag()
        val tag = CompoundTag()
        item.toTag(itemTag)
        tag.put(VindorUtils.wonderItemTag, itemTag)
        tag.putString(VindorUtils.wonderMsgTag, msg)
        return tag
    }

    companion object {
        fun fromTag(tag: CompoundTag): WonderProps {
            val item = ItemStack.fromTag(tag.getCompound(VindorUtils.wonderItemTag))
            val msg = tag.getString(VindorUtils.wonderMsgTag)
            return WonderProps(item, msg)
        }
    }
}
