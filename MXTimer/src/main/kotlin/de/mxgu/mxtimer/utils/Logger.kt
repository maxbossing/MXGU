package de.mxgu.mxtimer.utils

import de.maxbossing.mxpaper.MXColors
import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.bukkit.plus
import de.maxbossing.mxpaper.extensions.bukkit.toComponent
import de.maxbossing.mxpaper.extensions.console
import de.maxbossing.mxpaper.main.prefix
import de.mxgu.mxtimer.MXTimerMain.Companion.mxtimer
import net.kyori.adventure.text.Component

fun debug(vararg msg: Component) {
    if (!de.mxgu.mxtimer.debug)
        return

    msg.forEach {
        console.sendMessage(prefix + cmp("[DEBUG] ", MXColors.GRAY) +  it.color(MXColors.GRAY))
    }
}

fun info(vararg msg: Component) {
    msg.forEach {
        console.sendMessage(prefix + cmp("[INFO] ") +  it)
    }
}

fun warning(vararg msg: Component) {
    msg.forEach {
        console.sendMessage(prefix + cmp("[WARN] ", MXColors.YELLOW) +  it.color(MXColors.YELLOW))
    }
}

fun error(vararg msg: Component) {
    msg.forEach {
        console.sendMessage(prefix + cmp("[ERROR] ", MXColors.RED) +  it.color(MXColors.RED))
    }
}


fun debug(vararg msg: String) {
    msg.map { it.toComponent() }.forEach { debug(it) }
}
fun info(vararg msg: String) {
    msg.map { it.toComponent() }.forEach { info(it) }
}
fun warning(vararg msg: String) {
    msg.map { it.toComponent() }.forEach { warning(it) }
}
fun error(vararg msg: String) {
    msg.map { it.toComponent() }.forEach { error(it) }
}