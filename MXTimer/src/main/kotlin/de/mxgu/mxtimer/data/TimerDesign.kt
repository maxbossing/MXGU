package de.mxgu.mxtimer.data

import de.mxgu.mxtimer.timer.TimerManager
import kotlinx.serialization.Serializable
import de.mxgu.mxtimer.timer.Timer

@Serializable
data class TimerDesign (
    var name: String,
    val description: String,
    var creator: String,
    var displaySlot: DisplaySlot,
    var running: StageDesign,
    var paused: StageDesign,
    var stopped: StageDesign
)

@Serializable
data class StageDesign (
    var raw: String,
    var prefix: String,
    var suffix: String,
    var animated: Boolean,
    var animationSpeed: Float,
    var days: TimeDesign,
    var hours: TimeDesign,
    var minutes: TimeDesign,
    var seconds: TimeDesign,
    var milliseconds: TimeDesign
)

@Serializable
data class TimeDesign (
    var prefix: String,
    var suffix: String,
    var forceDoubleDigits: Boolean,
    var alwaysVisible: Boolean
)

/**
 * Where an [Timer] is displayed
 */
enum class DisplaySlot {
    /**
     * In the Hotbar of players
     */
    HOTBAR,

    /**
     * In the Bossbar and the top of the screen
     */
    BOSSBAR
}

fun TimerDesign.id(): String = TimerManager.getUUID(this)
