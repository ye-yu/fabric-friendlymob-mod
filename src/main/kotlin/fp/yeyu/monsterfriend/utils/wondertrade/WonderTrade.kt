package fp.yeyu.monsterfriend.utils.wondertrade

import fp.yeyu.monsterfriend.BefriendMinecraft
import fp.yeyu.monsterfriend.utils.ConfigFile
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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

    private val multiplayerForbiddenItems = listOf(
        // command blocks
        Items.COMMAND_BLOCK,
        Items.COMMAND_BLOCK_MINECART,
        Items.CHAIN_COMMAND_BLOCK,
        Items.REPEATING_COMMAND_BLOCK,

        // structure blocks
        Items.STRUCTURE_BLOCK,
        Items.STRUCTURE_VOID,
        Items.JIGSAW,

        // can't give an empty written book
        Items.WRITTEN_BOOK,

        // forbidden blocks
        Items.AIR,
        Items.BEDROCK,
        Items.END_PORTAL_FRAME,
        Items.BARRIER,

        // cartographer
        Items.MAP,
        Items.FILLED_MAP
    )
    const val wonderItemTag: String = "wonder_item"
    const val wonderMsgTag = "wonder_msg"
    private var stackLength: Int =
        ConfigFile.getInt(ConfigFile.Defaults.WONDER_SPACE)
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
        var item: Item
        do {
            item = Registry.ITEM.getRandom(random)
        } while (multiplayerForbiddenItems.contains(item))
        val randomItem = item
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
