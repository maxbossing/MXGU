package de.mxgu.mxtimer.utils

import de.maxbossing.mxpaper.extensions.bukkit.plainText
import de.maxbossing.mxpaper.extensions.bukkit.render
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.Locale

fun msg(key: String, locale: Locale = Locale.ENGLISH): Component = MiniMessage.miniMessage().deserialize(translatable("timer.$key").render(locale).plainText())
