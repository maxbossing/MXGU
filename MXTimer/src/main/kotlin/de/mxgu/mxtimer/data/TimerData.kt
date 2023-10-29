package de.mxgu.mxtimer.data

import de.maxbossing.mxpaper.serialization.UUIDSerializer
import de.mxgu.mxtimer.timer.TimerDirection
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.Duration

@Serializable
data class TimerData (
    val timerDesign: String,
    val time: Duration,
    val isVisible: Boolean,
    val direction: TimerDirection,
    val playerUUID: String?,
    val settings: TimerSettings,
    var activate: Boolean
)

@Serializable
data class TimerSettings (
    var allowJoin: Boolean,
    var freezeOnPause: Boolean,
)
