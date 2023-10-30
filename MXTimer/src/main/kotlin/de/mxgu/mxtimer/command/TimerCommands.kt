package de.mxgu.mxtimer.command

import de.maxbossing.mxpaper.extensions.bukkit.plus
import de.maxbossing.mxpaper.main.prefix
import de.mxgu.mxtimer.data.*
import de.mxgu.mxtimer.debug
import de.mxgu.mxtimer.gui.TimerGUI
import de.mxgu.mxtimer.timer.TimerManager
import de.mxgu.mxtimer.timer.globalTimer
import de.mxgu.mxtimer.utils.Permissions
import de.mxgu.mxtimer.utils.msg
import dev.jorel.commandapi.kotlindsl.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object TimerCommands {
    val timerCommand = commandTree("timer") {
        val timer = globalTimer
        withPermission(Permissions.Commands.Timer.BASE)
        playerExecutor { player, _ ->
            if (player.hasPermission(Permissions.Commands.Timer.GUI))
                TimerGUI(timer, player)
        }
        literalArgument("reload") {
            withPermission(Permissions.Commands.Timer.RELOAD)
            anyExecutor { _, _ ->
                TimerManager.loadDesigns()
            }
        }
        literalArgument("resume") {
            withPermission(Permissions.Commands.Timer.STATE)
            playerExecutor { player, _ ->
                if (timer.state == TimerState.RUNNING) {
                    player.sendMessage(prefix + msg("command.error.alreadyrunning", player.locale()))
                    return@playerExecutor
                }

                timer.state = TimerState.RUNNING

                player.sendMessage(prefix + msg("command.resumed", player.locale()))
            }
            anyExecutor { commandSender, _ ->

                if (timer.state == TimerState.RUNNING) {
                    commandSender.sendMessage(prefix + msg("command.error.alreadyrunning"))
                    return@anyExecutor
                }
                timer.state = TimerState.RUNNING
                commandSender.sendMessage(prefix + msg("command.resumed"))
            }
        }
        literalArgument("pause") {
            withPermission(Permissions.Commands.Timer.STATE)
            playerExecutor { player, _ ->

                if (timer.state != TimerState.RUNNING) {
                    player.sendMessage(prefix + msg("command.error.alreadypaused", player.locale()))
                    return@playerExecutor
                }

                timer.state = TimerState.PAUSED

                player.sendMessage(prefix + msg("command.paused", player.locale()))
            }
            anyExecutor { commandSender, _ ->


                if (timer.state != TimerState.RUNNING) {
                    commandSender.sendMessage(prefix + msg("command.error.alreadypaused"))
                    return@anyExecutor
                }
                timer.state = TimerState.PAUSED
                commandSender.sendMessage(prefix + msg("command.paused"))
            }
        }
        literalArgument("add") {
            withPermission(Permissions.Commands.Timer.TIME)
            integerArgument("amount", optional = false) {
                multiLiteralArgument("type", listOf("days", "hours", "minutes", "seconds", "milliseconds"),false) {
                    anyExecutor {_, commandArguments ->
                        when (commandArguments["type"]) {
                            "days" -> timer.addTime((commandArguments["amount"]as Int).days)
                            "hours" -> timer.addTime((commandArguments["amount"]as Int).hours)
                            "minutes" -> timer.addTime((commandArguments["amount"]as Int).minutes)
                            "seconds" -> timer.addTime((commandArguments["amount"]as Int).seconds)
                            "milliseconds" -> timer.addTime((commandArguments["amount"]as Int).milliseconds)
                        }
                    }
                }
            }
        }
        literalArgument("subtract") {
            withPermission(Permissions.Commands.Timer.TIME)
            integerArgument("amount", optional = false) {
                multiLiteralArgument("type", listOf("days", "hours", "minutes", "seconds", "milliseconds"),false) {
                    anyExecutor {_, commandArguments ->
                        when (commandArguments["type"]) {
                            "days" -> timer.subTime((commandArguments["amount"]as Int).days)
                            "hours" -> timer.subTime((commandArguments["amount"]as Int).hours)
                            "minutes" -> timer.subTime((commandArguments["amount"]as Int).minutes)
                            "seconds" -> timer.subTime((commandArguments["amount"]as Int).seconds)
                            "milliseconds" -> timer.subTime((commandArguments["amount"]as Int).milliseconds)
                        }
                    }
                }
            }
        }
        literalArgument("settings") {
            withPermission(Permissions.Commands.Timer.SETTINGS)
            multiLiteralArgument("setting", listOf("allowJoin", "freezeOnPause"), false) {
                booleanArgument("value", false) {
                    anyExecutor { _, commandArguments ->
                        when (commandArguments["setting"]) {
                            "allowJoin" -> timer.settings.allowJoin = commandArguments["value"] as Boolean
                            "freezeOnPause" -> timer.settings.allowJoin = commandArguments["value"] as Boolean
                        }
                    }
                }
            }
        }
        literalArgument("reset") {
            withPermission(Permissions.Commands.Timer.RESET)
            anyExecutor { _, _ ->
                timer.state = TimerState.STOPPED
                timer.time = Duration.ZERO
            }
        }
        literalArgument("direction",) {
            withPermission(Permissions.Commands.Timer.DIRECTION)
            multiLiteralArgument("direction", listOf("countUp", "countDown"), false) {
                anyExecutor { _, commandArguments ->
                    timer.direction = if ((commandArguments["direction"] as String) == "countUp") TimerDirection.COUNTUP else TimerDirection.COUNTDOWN
                }
            }
        }
    }

    val personalTimerCommand = commandTree("ptimer") {
        withRequirement { ConfigManager.config.personalTimers }
        withPermission(Permissions.Commands.PersonalTimer.BASE)
        playerExecutor { player, _ ->
            TimerGUI(TimerManager.getOrAddPersonalTimer(
                player.uniqueId,
                TimerData(
                    "default",
                    ZERO,
                    true,
                    TimerDirection.COUNTUP,
                    player.uniqueId,
                    TimerSettings(true, true),
                    true
                )
            ), player)
        }
        literalArgument("enable") {
            playerExecutor { player, _ ->
                val timer = TimerManager.getOrAddPersonalTimer(
                    player.uniqueId,
                    TimerData(
                        "default",
                        ZERO,
                        true,
                        TimerDirection.COUNTUP,
                        player.uniqueId,
                        TimerSettings(true, true),
                        true
                    )
                )
                timer.visible = true
            }
        }
        literalArgument("disable") {
            playerExecutor { player, _ ->
                val timer = TimerManager.getOrAddPersonalTimer(
                    player.uniqueId,
                    TimerData(
                        "default",
                        ZERO,
                        true,
                        TimerDirection.COUNTUP,
                        player.uniqueId,
                        TimerSettings(true, true),
                        true
                    )
                )
                timer.visible = false
            }
        }

        literalArgument("resume") {
            playerExecutor { player, _ ->
                val timer = TimerManager.getOrAddPersonalTimer(
                    player.uniqueId,
                    TimerData(
                        "default",
                        ZERO,
                        true,
                        TimerDirection.COUNTUP,
                        player.uniqueId,
                        TimerSettings(true, true),
                        true
                    )
                )
                if (timer.state == TimerState.RUNNING) {
                    player.sendMessage(prefix + msg("command.error.alreadyrunning", player.locale()))
                    return@playerExecutor
                }

                timer.state = TimerState.RUNNING

                player.sendMessage(prefix + msg("command.resumed", player.locale()))
            }
        }
        literalArgument("pause") {
            playerExecutor { player, _ ->
                val timer = TimerManager.getOrAddPersonalTimer(
                    player.uniqueId,
                    TimerData(
                        "default",
                        ZERO,
                        true,
                        TimerDirection.COUNTUP,
                        player.uniqueId,
                        TimerSettings(true, true),
                        true
                    )
                )
                if (timer.state != TimerState.RUNNING) {
                    player.sendMessage(prefix + msg("command.error.alreadypaused", player.locale()))
                    return@playerExecutor
                }

                timer.state = TimerState.PAUSED

                player.sendMessage(prefix + msg("command.paused", player.locale()))
            }
        }
        literalArgument("add") {
            integerArgument("amount", optional = false) {
                multiLiteralArgument("type", listOf("days", "hours", "minutes", "seconds", "milliseconds"),false) {
                    playerExecutor {player, arguments ->
                        val timer = TimerManager.getOrAddPersonalTimer(
                            player.uniqueId,
                            TimerData(
                                "default",
                                ZERO,
                                true,
                                TimerDirection.COUNTUP,
                                player.uniqueId,
                                TimerSettings(true, true),
                                true
                            )
                        )

                        when (arguments["type"]) {
                            "days" -> timer.addTime((arguments["amount"]as Int).days)
                            "hours" -> timer.addTime((arguments["amount"]as Int).hours)
                            "minutes" -> timer.addTime((arguments["amount"]as Int).minutes)
                            "seconds" -> timer.addTime((arguments["amount"]as Int).seconds)
                            "milliseconds" -> timer.addTime((arguments["amount"]as Int).milliseconds)
                        }
                    }
                }
            }
        }
        literalArgument("subtract") {
            integerArgument("amount", optional = false) {
                multiLiteralArgument("type", listOf("days", "hours", "minutes", "seconds", "milliseconds"),false) {
                    playerExecutor {player, arguments ->
                        val timer = TimerManager.getOrAddPersonalTimer(
                            player.uniqueId,
                            TimerData(
                                "default",
                                ZERO,
                                true,
                                TimerDirection.COUNTUP,
                                player.uniqueId,
                                TimerSettings(true, true),
                                true
                            )
                        )
                        when (arguments["type"]) {
                            "days" -> timer.subTime((arguments["amount"]as Int).days)
                            "hours" -> timer.subTime((arguments["amount"]as Int).hours)
                            "minutes" -> timer.subTime((arguments["amount"]as Int).minutes)
                            "seconds" -> timer.subTime((arguments["amount"]as Int).seconds)
                            "milliseconds" -> timer.subTime((arguments["amount"]as Int).milliseconds)
                        }
                    }
                }
            }
        }
        literalArgument("reset") {
            playerExecutor { player, _ ->
                val timer = TimerManager.getOrAddPersonalTimer(
                    player.uniqueId,
                    TimerData(
                        "default",
                        ZERO,
                        true,
                        TimerDirection.COUNTUP,
                        player.uniqueId,
                        TimerSettings(true, true),
                        true
                    )
                )
                timer.state = TimerState.STOPPED
                timer.time = Duration.ZERO
            }
        }
        literalArgument("direction",) {
            multiLiteralArgument("direction", listOf("countUp", "countDown"), false) {
                playerExecutor { player, arguments ->
                    val timer = TimerManager.getOrAddPersonalTimer(
                        player.uniqueId,
                        TimerData(
                            "default",
                            ZERO,
                            true,
                            TimerDirection.COUNTUP,
                            player.uniqueId,
                            TimerSettings(true, true),
                            true
                        )
                    )
                    timer.direction = if ((arguments["direction"] as String) == "countUp") TimerDirection.COUNTUP else TimerDirection.COUNTDOWN
                }
            }
        }
    }

    val debugCommand = commandTree("debug") {
        withRequirement { debug }
        anyExecutor { _ , _ ->
            debug  = !debug
        }
        literalArgument("timers") {
            literalArgument("list") {
                anyExecutor { p, _ ->
                    TimerManager.timers.forEach { t, u ->
                        p.sendMessage("========== $t ===========")
                        p.sendMessage("Time         -> ${u.time}")
                        p.sendMessage("Visible      -> ${u.visible}")
                        p.sendMessage("Design       -> ${u.design.name}")
                        p.sendMessage("State        -> ${u.state}")
                        p.sendMessage("Allow Join   -> ${u.settings.allowJoin}")
                        p.sendMessage("World Freeze -> ${u.settings.freezeOnPause}")
                    }
                }
            }
        }
        literalArgument("designs") {
            literalArgument("list") {
                anyExecutor {p, _ ->
                    TimerManager.designs.forEach { t, u ->
                        p.sendMessage("========== $t ==========")
                        p.sendMessage("Name        -> ${u.name}")
                        p.sendMessage("Description -> ${u.description}")
                        p.sendMessage("Author      -> ${u.creator}")
                        p.sendMessage("DisplaySlot -> ${u.displaySlot}")
                    }
                }
            }
        }
    }


}