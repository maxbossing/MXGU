package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.event.listen
import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.onlinePlayers
import de.maxbossing.mxpaper.extensions.pluginKey
import de.maxbossing.mxpaper.runnables.task
import de.mxgu.mxtimer.data.TimerData
import de.mxgu.mxtimer.data.TimerDesign
import de.mxgu.mxtimer.data.TimerSettings
import de.mxgu.mxtimer.debug
import de.mxgu.mxtimer.utils.debug
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.bossbar.BossBar.Overlay
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

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

            if (player == null) {
                onlinePlayers.forEach {
                    if (TimerManager.timers[it.uniqueId.toString()]?.visible == true) {
                        bossbar.removeViewer(it)
                        return@forEach
                    }
                    if (design.displaySlot == DisplaySlot.HOTBAR) {
                        bossbar.removeViewer(it)
                        it.sendActionBar(buildFormat(design, time))
                    } else if (design.displaySlot == DisplaySlot.BOSSBAR) {
                        bossbar.name(buildFormat(design, time))
                        bossbar.addViewer(it)
                    }
                }
            } else {
                if (design.displaySlot == DisplaySlot.HOTBAR) {
                    bossbar.removeViewer(player.player!!)
                    player.player!!.sendActionBar(buildFormat(design, time))
                } else if (design.displaySlot == DisplaySlot.BOSSBAR){
                    bossbar.name(buildFormat(design, time))
                    bossbar.addViewer(player.player!!)
                }
            }

            if (time <= ZERO && direction == TimerDirection.COUNTDOWN) {
                state = TimerState.STOPPED
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