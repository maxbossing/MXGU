package de.mxgu.mxtimer.timer

import de.maxbossing.mxpaper.event.listen
import de.maxbossing.mxpaper.event.register
import de.maxbossing.mxpaper.event.unregister
import de.mxgu.mxtimer.utils.Permissions
import de.mxgu.mxtimer.utils.msg
import org.bukkit.GameMode
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.permissions.Permission

object TimerListeners {

    fun disableJoin() {
        onJoin.register()
    }

    fun enableJoin() {
        onJoin.unregister()
    }

    fun freezeWorld() {
        onBreak.register()
        onDamage.register()
        onPlace.register()
        onHunger.register()
        onDamage.register()
        onInteract.register()
        onSpawn.register()
    }

    fun unfreezeWorld() {
        onBreak.unregister()
        onDamage.unregister()
        onPlace.unregister()
        onHunger.unregister()
        onDamage.unregister()
        onInteract.unregister()
        onSpawn.unregister()
    }


    private val onJoin = listen<PlayerLoginEvent>(register = false, priority = EventPriority.MONITOR) {
        if (it.player.hasPermission(Permissions.Bypass.ALLOWJOIN)) return@listen
        it.disallow(PlayerLoginEvent.Result.KICK_OTHER, msg("disallowjoinmessage", it.player.locale()))
    }
    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (it.entity.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        if (it.cause != EntityDamageEvent.DamageCause.VOID)
            it.isCancelled = true
    }
    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        if (it.player.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE && gm != GameMode.SPECTATOR
    }
    private val onBreak = listen<BlockBreakEvent>(register = false) {
        if (it.player.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        if (it.isCancelled) return@listen
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }
    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        if (it.player.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        if (it.isCancelled) return@listen
        val gm = it.player.gameMode
        it.isCancelled = gm != GameMode.CREATIVE
    }
    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        if (it.entity.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        it.isCancelled = true
    }
    private val onHunger = listen<FoodLevelChangeEvent>(register = false) {
        if (it.entity.hasPermission(Permissions.Bypass.WORLDFREEZE))return@listen
        it.isCancelled = true
    }
}