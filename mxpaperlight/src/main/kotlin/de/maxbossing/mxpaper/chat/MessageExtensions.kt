@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package de.maxbossing.mxpaper.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.command.CommandSender

//TODO: Audience instead of CommandSender

/**
 * Sends multiple Components at once, splitted by a newline
 */
fun CommandSender.sendMessage(vararg components: Component) {
    this.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), *components))
}

/**
 * Opens a [LiteralTextBuilder].
 *
 * @param baseText the text you want to begin with, it is okay to let this empty
 * @param builder the builder which can be used to set the style and add child text components
 */
inline fun CommandSender.sendText(
    baseText: String = "",
    crossinline builder: de.maxbossing.mxpaper.chat.LiteralTextBuilder.() -> Unit = { }
) = this.sendMessage(de.maxbossing.mxpaper.chat.literalText(baseText, builder))