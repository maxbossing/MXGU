@file:Suppress("unused")
package de.maxbossing.mxpaper.extensions.bukkit

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.inventory.meta.BookMeta

/**
 * returns the content of a [BookMeta], every page being a Line splitted by \n
 */
val BookMeta.content
    get() =
        StringBuilder().apply {
            for (it in pages().map { LegacyComponentSerializer.legacy('§').serialize(it) }) {
                if (isNotEmpty())
                    append('\n')
                append(it)
            }
        }.toString()