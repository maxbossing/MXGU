@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package de.maxbossing.mxpaper.chat

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import java.awt.Color

/**
 * Creates a new [TextColor] from the provided rgb value
 * @param r the R value
 * @param g the G value
 * @param b the B value
 * @return The [TextColor] corresponding to these calues
 */
fun tcol(r: Int, g: Int, b: Int): TextColor = TextColor.color(r,g,b)

/**
 * Creates a new [TextColor] from the provided hex String. Format example: "#4BD&CD"
 * @param hex the Hex String
 * @return The [TextColor] corresponding to these values
 */
fun tcol(hex: String): TextColor? = TextColor.fromHexString(hex)
