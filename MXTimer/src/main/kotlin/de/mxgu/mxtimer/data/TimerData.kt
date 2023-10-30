package de.mxgu.mxtimer.data

import de.maxbossing.mxpaper.serialization.UUIDSerializer
import de.mxgu.mxtimer.timer.Timer
import de.mxgu.mxtimer.timer.TimerManager
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration

@Serializable
data class TimerData (
    val timerDesign: String,
    val time: Duration,
    val isVisible: Boolean,
    val direction: TimerDirection,
    @Serializable(with = UUIDSerializer::class) val playerUUID: UUID?,
    val settings: TimerSettings,
    var activate: Boolean
)

@Serializable
data class TimerSettings (
    var allowJoin: Boolean,
    var freezeOnPause: Boolean,
)

/**
 * The direction in which a [de.mxgu.mxtimer.timer.Timer] runs
 */
enum class TimerDirection {
    /**
     * The timer increments
     */
    COUNTUP,

    /**
     * The timer decrements
     */
    COUNTDOWN
}

/**
 * The state a [de.mxgu.mxtimer.timer.Timer] is in
 */
enum class TimerState {
    /**
     * The timer hasn't started yet
     *
     * The Timer is in this state only once, at startup, before it is started
     */
    STOPPED,

    /**
     * The timer is paused from running
     */
    PAUSED,

    /**
     * The timer is running
     */
    RUNNING
}


fun Timer.serialize() : TimerData {
    return TimerData(
        design.id()!!,
        time,
        visible,
        direction,
        playerUUID,
        settings,
        activate

    )
}

fun TimerData.deserialize(): Timer {
    return Timer (
        TimerManager.getDesign(timerDesign),
        activate,
        direction,
        time,
        playerUUID,
        isVisible,
        settings
    )
}