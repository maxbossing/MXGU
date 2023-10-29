@file:Suppress("unused")

package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.main.prefix
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Displays a title with [Duration]
 * @param main The Main Title
 * @param sub The sub title
 * @param fadeIn How long the Title should fade in
 * @param stay How long the Title should stay
 * @param fadeOut How long the Title should fade out
 */
fun Audience.title(main: Component, sub: Component, fadeIn: Duration = Duration.ZERO, stay: Duration = 5.seconds, fadeOut: Duration = Duration.ZERO) {
    showTitle(Title.title(main, sub, Title.Times.times(fadeIn.toJavaDuration(), stay.toJavaDuration(), fadeOut.toJavaDuration())))
}

/**
 * Add two audiences together
 */
operator fun Audience.plus(audience: Audience) = Audience.audience(this, audience)

/**
 * Add multiple audiences together
 */
operator fun Audience.plus(audiences: Collection<Audience>) = Audience.audience(audiences.plus(this))

/**
 * Send a message to an Audience prefixed by the set [prefix]
 * @param msg The message to send
 */
fun Audience.prefixedmsg(msg: Component) {
    this.sendMessage(prefix +  msg)
}

/**
 * Sends a message to a player with the set [prefix]
 * @param messages the messages to send
 */
fun Audience.prefixedmsg(vararg messages: Component) {
    messages.forEach {
        this.prefixedmsg(it)
    }
}