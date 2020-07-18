package fp.yeyu.monsterfriend.statics.immutable

import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fp.yeyu.monsterfriend.BefriendMinecraft
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
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

        val randomMessageFileName = getString(Defaults.RANDOM_MESSAGE_LIST)
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

        val resourceFile = File(this::class.java.getResource("/randomMessage.txt").toURI())
        val destFile = getFile("randomMessage.txt")
        FileUtils.copyFile(resourceFile, destFile)
    }

    private fun validate() {
        val jsonObject = JsonParser().parse(JsonReader(FileReader(getConfigFile()))).asJsonObject
        Defaults.values().forEach {
            assertNonNull(jsonObject[it.variableName])
        }
    }

    private fun assertNonNull(a: Any?) {
        if (a == null) throw NullPointerException()
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

    enum class Defaults(val def: Any) {
        WONDER_SPACE(5),
        SIMULATE_MULTIPLAYER(true),
        RANDOM_MESSAGE_LIST("randomMessage.txt"),
        VINDOR_TRANSFORM_CHANCE(0.3f);

        val variableName = name.toLowerCase()
    }
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
