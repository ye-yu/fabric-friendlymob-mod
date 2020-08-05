package fp.yeyu.monsterfriend.utils

import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fp.yeyu.monsterfriend.BefriendMinecraft
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

object ConfigFile {
    private val map = HashMap<String, JsonPrimitive>()
    private val randomMessages = mutableListOf<String>()
    private val logger: Logger = LogManager.getLogger()

    init {
        try {
            validate()
        } catch (e: Exception) {
            newConfigFile()
        }

        val json = JsonParser().parse(JsonReader(FileReader(getConfigFile()))).asJsonObject

        Defaults.values().forEach {
            map[it.variableName] = json[it.variableName].asJsonPrimitive
            logger.info("[${BefriendMinecraft::class.java.simpleName}] Plugin configuration - ${it.variableName}: ${map[it.variableName].toString()}")
        }

        val randomMessageFileName =
            getString(Defaults.RANDOM_MESSAGE_LIST)
        with(Scanner(getFile(randomMessageFileName))) {
            while (this.hasNextLine()) {
                val line = this.nextLine()
                randomMessages.add(line)
            }
        }
    }

    fun getRandomMessages(): MutableList<String> {
        return randomMessages
    }

    fun getInt(attr: Defaults): Int {
        if (!map[attr.variableName]!!.isNumber) throw ClassCastException("[${BefriendMinecraft::class.java.simpleName}]'${attr.variableName}' is not a number! Check config.json")
        return map[attr.variableName]!!.asInt
    }

    fun getFloat(attr: Defaults): Float {
        if (!map[attr.variableName]!!.isNumber) throw ClassCastException("[${BefriendMinecraft::class.java.simpleName}]'${attr.variableName}' is not a number! Check config.json")
        return map[attr.variableName]!!.asFloat
    }

    fun getBoolean(attr: Defaults): Boolean {
        if (!map[attr.variableName]!!.isBoolean) throw ClassCastException("[${BefriendMinecraft::class.java.simpleName}]'${attr.variableName}' is not a boolean! Check config.json")
        return map[attr.variableName]!!.asBoolean
    }

    fun getString(attr: Defaults): String {
        return map[attr.variableName]!!.asString!!
    }

    fun visit() {} // just so that the config file is initialised

    private fun newConfigFile() {
        val json = JsonWriter(FileWriter(getConfigFile()))
        json.setIndent("  ")
        json.beginObject()

        Defaults.values().forEach {
            json.name(it.variableName).value(it.def)
        }

        json.endObject().close()

        val destFile = getFile("randomMessage.txt")
        if (!destFile.exists()) destFile.createNewFile()
        this::class.java.getResourceAsStream("/randomMessage.txt").copyTo(FileOutputStream(destFile))
    }

    private fun validate() {
        val jsonObject = JsonParser().parse(JsonReader(FileReader(getConfigFile()))).asJsonObject
        Defaults.values().forEach {
            requireNotNull(jsonObject[it.variableName])
            check(jsonObject[it.variableName].isJsonPrimitive)
            check(it.validator(jsonObject[it.variableName].asJsonPrimitive))
        }
    }

    private fun getConfigFile(): File {
        return getFile("config.json")
    }

    private fun getFile(filename: String): File {
        val parent = Paths.get(".").resolve("mods").resolve(BefriendMinecraft::class.simpleName!!).toFile()
        if (!parent.exists()) {
            parent.mkdirs()
        }
        return File(parent, filename)
    }

    object Validator {
        val isNumber = { i: JsonPrimitive -> i.isNumber }
        val isBoolean = { i: JsonPrimitive -> i.isBoolean }
        val isString = { i: JsonPrimitive -> i.isString }
    }

    enum class Defaults(val def: Any, val validator: (JsonPrimitive) -> Boolean) {
        WONDER_SPACE(5, Validator.isNumber),
        SIMULATE_MULTIPLAYER(true, Validator.isBoolean),
        RANDOM_MESSAGE_LIST("randomMessage.txt", Validator.isString),
        VINDOR_TRANSFORM_CHANCE(0.01f, Validator.isNumber),
        EVIONE_MAX_SPELL_TICK(70, Validator.isNumber),
        EVIONE_SYNTHESIS_CHANCE(0.1f, Validator.isNumber),
        EVIONE_SYNTHESIS_CAN_SPEED_UP_CHANCE(0.005f, Validator.isNumber),
        EVIONE_SYNTHESIS_SPEED_UP_COUNT(6, Validator.isNumber),
        EVIONE_SYNTHESIS_SPEED_UP_CHANCE(0.003f, Validator.isNumber),
        EVIONE_TRANSFORM_CHANCE(0.23f, Validator.isNumber),
        EVIONE_DROP_VEX_ESSENCE_CHANCE(0.002f, Validator.isNumber),
        VEX_ESSENCE_CAUGHT_CHANCE(0.42f, Validator.isNumber);

        val variableName = name.toLowerCase()

    }

    private fun JsonWriter.value(def: Any) {
        when (def) {
            is Float -> {
                this.value(def)
            }
            is Int -> {
                this.value(def)
            }
            is Boolean -> {
                this.value(def)
            }
            else -> {
                this.value(def.toString())
            }
        }
    }

}
