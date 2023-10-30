@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.MXColors
import de.maxbossing.mxpaper.event.listen
import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.bukkit.title
import de.maxbossing.mxpaper.extensions.bukkit.toComponent
import de.maxbossing.mxpaper.extensions.deserialized
import de.maxbossing.mxpaper.extensions.onlinePlayers
import de.maxbossing.mxpaper.runnables.task
import de.mxgu.mxtimer.data.*
import de.mxgu.mxtimer.mxtimer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Timer(
    var design: TimerDesign,
    var activate: Boolean,
    var direction: TimerDirection,
    var time: Duration,
    var playerUUID: UUID?,
    var visible: Boolean,
    var settings: TimerSettings
){

    /**
     * Internal tracker, not really needed besides a check
     */
    private var isRunning = false

    /**
     * Current animator, sed for MiniMessage animations
     */
    var animator = 1.0f

    /**
     * Bossbar if [DisplaySlot] is [DisplaySlot.BOSSBAR]
     */
    val bossbar = BossBar.bossBar(cmp("loading.."), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)

    /**
     * The player to send the Timer to
     *
     * Is null if the timer is the global timer
     */
    val player = playerUUID?.let { Bukkit.getOfflinePlayer(it) }

    private val bossbarListener = listen<PlayerQuitEvent> {
        bossbar.removeViewer(it.player)
    }

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

    /**
     * Listeners of the Timer
     *
     * Private timers do not have listeners as they are purely for tracking time
     */
    val listeners: TimerListeners? = if (playerUUID != null) null else TimerListeners


    /**
     * Task driving the timer
     */
    private fun run() {
        task(true, 0, 1) {
            // If nobody sees the timer, do not increment
            if (!visible)return@task
            // If the timer has a player, and this player is offline, do not increment
            if (player?.isOnline == false) return@task

            // This creates the audience to send the timer to
            val target: Audience? = getAudience()


            // janky workaround because I'm to lazy to implement proper handling of the Bossbars :)
            onlinePlayers.filter {  TimerManager.hasPersonalTimer(it.uniqueId) && TimerManager.getPersonalTimer(it.uniqueId)?.visible == true }.forEach { bossbar.removeViewer(it) }

            // Animations
            animate()

            // Sends the Timer
            if (target != null)
                sendTimer(target)

            // Sends titles counting down and plays sounds if the timer is below 10 and counting down
            if (time < 10.seconds && direction == TimerDirection.COUNTDOWN && time != 0.seconds && state == TimerState.RUNNING) {
                target?.title(
                    time.toInt(DurationUnit.SECONDS)
                        .toString()
                        .toComponent()
                        .color(
                            // Color is based on how much time is left
                            // 10-6 -> Green
                            // 5-3 -> Yellow
                            // 3-1 -> Red
                            if (time <= 3.seconds) MXColors.RED
                            else if (time <= 5.seconds) MXColors.YELLOW
                            else MXColors.GREEN
                        ),
                    cmp(""),
                    stay = 1.seconds
                )
                // "Ding" sound if time is ticking below 10
                if (time in listOf( 2.seconds, 3.seconds, 4.seconds, 5.seconds,6.seconds, 7.seconds, 8.seconds, 9.seconds) )
                    target?.playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f))
                else if (time == 1.seconds)
                    // Ender dragon sound to indicate time is up
                    target?.playSound(Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.MASTER, 1f, 1f))
            }

            if (time <= ZERO && direction == TimerDirection.COUNTDOWN) {
                // Stops the timer
                state = TimerState.STOPPED
                // The 0 is not being removed when the timer stops, I don't have time to handle that properly
                target?.title(cmp(""), cmp(""))
            }

            // Only increment if timer is running
            if (state == TimerState.RUNNING)
                time += if (direction == TimerDirection.COUNTUP) 50.milliseconds else (-50).milliseconds
        }
    }

    /**
     * The [Audience] to send the Timer to.
     *
     * If the timer is global, the Audience will be every player who has no visible personal timer
     *
     * Else it will be just the player whose UUID was passed as constructor parameter
     *
     * @return The Audience to send the timer to
     */
    fun getAudience(): Audience? {
        return if (player == null)
            Audience.audience(
                onlinePlayers.filterNot {
                    TimerManager.hasPersonalTimer(it.uniqueId) && TimerManager.getPersonalTimer(it.uniqueId)?.visible == true
                }
            )
        else if (visible)
            Audience.audience(player.player!!)
        else null
    }


    /**
     * Increments the animator used to make gradients fade in MiniMessage
     */
    fun animate() {
        // Every stage has its own animation Speed
        animator += when (state) {
            TimerState.RUNNING -> design.running.animationSpeed
            TimerState.PAUSED -> design.paused.animationSpeed
            TimerState.STOPPED -> design.stopped.animationSpeed
        }

        // MiniMessage only allows values in between -1.0f and 1.0f
        // So this just wraps around
        if (animator < -1.0f) animator += 2.0f
        else if (animator > 1.0f) animator -= 2.0f
    }

    /**
     * Sends this timer to the given [Audience]
     * @param target The [Audience] to send the Timer to
     */
    fun sendTimer(target: Audience) {
        if (design.displaySlot == DisplaySlot.HOTBAR) {
            bossbar.removeViewer(target)
            target.sendActionBar(buildFormat(design, time))
        } else if (design.displaySlot == DisplaySlot.BOSSBAR) {
            bossbar.name(buildFormat(design, time))
            bossbar.addViewer(target)
        }

    }

    /**
     * Creates a [Component] ready to be displayed in a Timer
     * @param design The [TimerDesign] which will be used to format and parse the time
     * @param time The [Duration] to parse
     */
    fun buildFormat(design: TimerDesign, time: Duration): Component {
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
            raw = raw.replace("<d>", buildTimeFormat(format.days, days.toInt()))
            raw = raw.replace("<h>", buildTimeFormat(format.hours, hours))
            raw = raw.replace("<m>", buildTimeFormat(format.minutes, minutes))
            raw = raw.replace("<s>", buildTimeFormat(format.seconds, seconds))
            raw = raw.replace("<ms>", buildTimeFormat(format.milliseconds, (milliseconds / 1000000)))
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
    fun buildTimeFormat(design: TimeDesign, time: Int): String {
        return if ((!design.alwaysVisible && time <= 0)) "" else "${design.prefix}${if (design.forceDoubleDigits && time < 10) 0 else ""}$time${design.suffix}"
    }

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

    init {
        if (activate) {
            run()
            isRunning = true
            listeners?.freezeWorld()
            listeners?.enableJoin()
        }
    }
}