package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.deserialized
import de.mxgu.mxtimer.data.*
import de.mxgu.mxtimer.debug
import de.mxgu.mxtimer.mxtimer
import de.mxgu.mxtimer.utils.debug
import net.kyori.adventure.text.Component
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration


/**
 * The state an [AbstractTimer] is in
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

/**
 * The direction in which an [AbstractTimer] runs
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
 * Where an [AbstractTimer] is displayed
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


abstract class AbstractTimer (
    var design: TimerDesign,
    /**
     * Whether the timer should be activated when it's loaded
     * The timer can be activated later by setting [AbstractTimer.state] to [TimerState.RUNNING]
     */
    val activate: Boolean,
    /**
     * The [TimerDirection] in which the Timer is running
     */
    var direction: TimerDirection,
    /**
     * The time of the timer
     */
    var time: Duration = Duration.ZERO,
    /**
     * To which player the timer should be displayed
     *
     * If this is null, the timer will register itself as global
     */
    var playerUUID: String?,
    /**
     * Whether the timer should be shown
     */
    var visible: Boolean,

    /**
     * Settings specific to the timer
     */
    var settings: TimerSettings
) {
    protected var isRunning = false
    protected var animator = 1.0f
    /**
     * The state of the Timer
     */
    var state: TimerState = TimerState.STOPPED
        set(value) {
            field = value
            if (!mxtimer.isEnabled)return
            if (field == TimerState.RUNNING) {
                listeners?.unfreezeWorld()

                if (!settings.allowJoin)
                    listeners?.disableJoin()

                if (!isRunning)
                    run()

            } else {
                if (settings.freezeOnPause)
                    listeners?.freezeWorld()
                listeners?.enableJoin()
            }
        }

    val listeners: TimerListeners? = if (playerUUID != null) null else TimerListeners

    /**
     * Function responsible for starting a task that increments/decrements the timer
     *
     * The task needs to account for the [TimerState] and draw the timer accordingly
     */
    abstract fun run()

    /**
     * Adds time to the Timer
     * @param days the amount of days to add
     * @param hours the amount of hours to add
     * @param minutes the amount of minutes to add
     * @param seconds the amount of seconds to add
     * @param milliseconds the amount of milliseconds to add
     * @return the new time
     */
    fun addTime(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0): Duration {
        time.plus(days.toDuration(DurationUnit.DAYS))
        time.plus(hours.toDuration(DurationUnit.HOURS))
        time.plus(minutes.toDuration(DurationUnit.MINUTES))
        time.plus(seconds.toDuration(DurationUnit.SECONDS))
        time.plus(milliseconds.toDuration(DurationUnit.MILLISECONDS))
        return time
    }
    /**
     * Removes time from the Timer
     * @param days the amount of days to remove
     * @param hours the amount of hours to remove
     * @param minutes the amount of minutes to remove
     * @param seconds the amount of seconds to remove
     * @param milliseconds the amount of milliseconds to remove
     * @return the new time
     */
    fun subTime(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0): Duration {
        time.minus(days.toDuration(DurationUnit.DAYS))
        time.minus(hours.toDuration(DurationUnit.HOURS))
        time.minus(minutes.toDuration(DurationUnit.MINUTES))
        time.minus(seconds.toDuration(DurationUnit.SECONDS))
        time.minus(milliseconds.toDuration(DurationUnit.MILLISECONDS))
        return time
    }

    /**
     * Adds time to the TImer
     * @param time the duration to add
     * @return the new time
     */
    fun addTime(time: Duration): Duration  {
         this.time += time
        return this.time
    }

    /**
     * Subtracts time from the timer
     * @param time the duration to remove
     * @return the new time
     */
    fun subTime(time: Duration): Duration {
        this.time -= time
        return this.time
    }


    /**
     * Creates a [Component] ready to be displayed in a Timer
     * @param design The [TimerDesign] which will be used to format and parse the time
     * @param time The [Duration] to parse
     */
    open fun buildFormat(design: TimerDesign, time: Duration): Component {
        val format =
            when (state) {
                TimerState.STOPPED -> design.stopped
                TimerState.PAUSED -> design.paused
                TimerState.RUNNING -> design.running
            }

        var raw = format.raw

        raw = raw.replace("<prefix>", format.prefix)
        raw = raw.replace("<suffix>", format.suffix)
        time.toComponents {days, hours, minutes, seconds, milliseconds ->
            raw = raw.replace("<d>", buildTimeFormat(format.days, days.toInt(), DurationUnit.DAYS))
            raw = raw.replace("<h>", buildTimeFormat(format.hours, hours, DurationUnit.HOURS))
            raw = raw.replace("<m>", buildTimeFormat(format.minutes, minutes, DurationUnit.MINUTES))
            raw = raw.replace("<s>", buildTimeFormat(format.seconds, seconds, DurationUnit.SECONDS))
            raw = raw.replace("<ms>", buildTimeFormat(format.milliseconds, (milliseconds / 1000000), DurationUnit.MILLISECONDS))
        }

        if (format.animated)
            raw = raw.replace("<*>", animator.toString())

        return raw.deserialized
    }

    /**
     * Creates a String representing the given time following the given [TimeDesign]
     * @param design The [TimeDesign] which will be used to format and parse the time
     * @param time The [Duration] to parse
     */
    open fun buildTimeFormat(design: TimeDesign, time: Int, unit: DurationUnit): String {
        return if ((!design.alwaysVisible && time <= 0)) "" else "${design.prefix}${if (design.forceDoubleDigits && time < 10) 0 else ""}$time${design.suffix}"
    }
}

fun AbstractTimer.serialize() : TimerData {
    return TimerData(
        design.id!!,
        time,
        visible,
        direction,
        playerUUID,
        settings,
        activate

    )
}

