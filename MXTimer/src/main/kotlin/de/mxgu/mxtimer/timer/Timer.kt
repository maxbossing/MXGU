package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.MXColors
import de.maxbossing.mxpaper.event.listen
import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.bukkit.plus
import de.maxbossing.mxpaper.extensions.bukkit.title
import de.maxbossing.mxpaper.extensions.bukkit.toComponent
import de.maxbossing.mxpaper.extensions.onlinePlayers
import de.maxbossing.mxpaper.extensions.pluginKey
import de.maxbossing.mxpaper.runnables.task
import de.mxgu.mxtimer.data.TimerData
import de.mxgu.mxtimer.data.TimerDesign
import de.mxgu.mxtimer.data.TimerSettings
import de.mxgu.mxtimer.debug
import de.mxgu.mxtimer.utils.debug
import de.mxgu.mxtimer.utils.msg
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class Timer(
    design: TimerDesign,
    activate: Boolean,
    direction: TimerDirection,
    time: Duration,
    playerUUID: String?,
    visible: Boolean,
    settings: TimerSettings
) : AbstractTimer (
    design,
    activate,
    direction,
    time,
    playerUUID,
    visible,
    settings
) {
    private val player = playerUUID?.let { if (playerUUID == "global-timer") null else Bukkit.getOfflinePlayer(UUID.fromString(it)) }

    private val bossbar = BossBar.bossBar(cmp("loading.."), 1f, Color.WHITE, Overlay.PROGRESS)

    private val bossbarListener = listen<PlayerQuitEvent> {
        bossbar.removeViewer(it.player)
    }

    override fun run() {
        task(true, 0, 1) {
            if (!visible)return@task
            if (player?.isOnline == false) return@task

            val target: Audience = if (player == null)
                Audience.audience(onlinePlayers.filterNot { TimerManager.timers.containsKey(it.uniqueId.toString()) && TimerManager.timers[it.uniqueId.toString()]?.visible == true })
            else
                player.player!!


            // Janky workaround because I'm to lazy to implement proper handling of the bossbars :)
            onlinePlayers.filter {  TimerManager.timers.containsKey(it.uniqueId.toString()) && TimerManager.timers[it.uniqueId.toString()]?.visible == true }.forEach { bossbar.removeViewer(it) }

            animator += when (state) {
                TimerState.RUNNING -> design.running.animationSpeed
                TimerState.PAUSED -> design.paused.animationSpeed
                TimerState.STOPPED -> design.stopped.animationSpeed
            }

            if (animator < -1.0f) animator -= 2.0f
            else if (animator > 1.0f) animator -= 2.0f

            if (design.displaySlot == DisplaySlot.HOTBAR) {
                bossbar.removeViewer(target)
                target.sendActionBar(buildFormat(design, time))
            } else if (design.displaySlot == DisplaySlot.BOSSBAR) {
                bossbar.name(buildFormat(design, time))
                bossbar.addViewer(target)
            }

            if (time < 10.seconds && direction == TimerDirection.COUNTDOWN && time != 0.seconds && state == TimerState.RUNNING) {
                target.title(
                    time.toInt(DurationUnit.SECONDS)
                        .toString()
                        .toComponent()
                        .color(
                            if (time < 3.seconds) MXColors.RED
                            else if (time < 5.seconds) MXColors.YELLOW
                            else MXColors.GREEN
                        ),
                    cmp(""),
                    stay = 1.seconds
                )
                if (time in listOf( 2.seconds, 3.seconds, 4.seconds, 5.seconds,6.seconds, 7.seconds, 8.seconds, 9.seconds) )
                    target.playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.MASTER, 1f, 1f))
                else if (time == 1.seconds)
                    target.playSound(Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.MASTER, 1f, 1f))
            }

            if (time <= ZERO && direction == TimerDirection.COUNTDOWN) {
                state = TimerState.STOPPED
                // The 0 is not being removed when the timer stops, I don't have time to handle that properly
                target.title(cmp(""), cmp(""))
            }


            if (state == TimerState.RUNNING)
                time += if (direction == TimerDirection.COUNTUP) 50.milliseconds else (-50).milliseconds
        }
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