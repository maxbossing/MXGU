package de.mxgu.mxtimer.utils

import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.timer.TimerManager
import dev.vishna.watchservice.KWatchChannel
import dev.vishna.watchservice.asWatchChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File

object DesignHotReloader {
    val watchChannel = File(mxtimer.dataFolder.path + "/designs").asWatchChannel(KWatchChannel.Mode.SingleDirectory)

    init {
        runBlocking {
            watchChannel.consumeEach {
                debug("Change detected in file [${it.file.name}]")
                if (TimerManager.designs.containsKey(it.file.nameWithoutExtension)) {
                    debug("Change in design, reloading...")
                    TimerManager.designs[it.file.nameWithoutExtension] = Json.decodeFromString(it.file.readText())
                } else {
                    debug("New design detected, loading...")
                    TimerManager.designs += it.file.nameWithoutExtension to Json.decodeFromString(it.file.readText())
                }
                debug("Design loaded")
            }
        }

    }
}