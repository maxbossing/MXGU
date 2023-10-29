package de.maxbossing.mxpaper.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Input: GRASS_BLOCK
 *
 * Output: Grass Block
 *
 * from: https://github.com/MiraculixxtT
 */
val String.fancy: String
    get(){
        val split = split('_') //GRASS_BLOCK -> [GRASS, BLOCK]
        return buildString {
            split.forEach { word ->
                append(word[0].uppercase() + word.substring(1).lowercase() + " ") //GRASS -> Grass
            }
        }.removeSuffix(" ")
    }

/**
 * Deserialize to a [net.kyori.adventure.text.Component] using [MiniMessage]
 */
val String.deserialized: Component
    get() = MiniMessage.miniMessage().deserialize(this)
