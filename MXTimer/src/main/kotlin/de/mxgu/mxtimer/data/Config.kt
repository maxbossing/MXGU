package de.mxgu.mxtimer.data

import de.mxgu.mxtimer.debug
import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.utils.debug
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@Serializable
data class Config(var personalTimers: Boolean)


object ConfigManager {

    var config: Config

    val file = File(mxtimer.dataFolder.path + "/config.json")

    init {
        if (!file.exists()) {
            debug("Config File does not exist, creating...")
            file.createNewFile()
            file.writeText(Json.encodeToString(Config.serializer(), Config(personalTimers = true)))
        }

        config = Json.decodeFromString(file.readText())
        debug("Config loaded")
    }

    fun save() {
        file.writeText(Json.encodeToString(Config.serializer(), config))
        debug("Config saved")
    }
}