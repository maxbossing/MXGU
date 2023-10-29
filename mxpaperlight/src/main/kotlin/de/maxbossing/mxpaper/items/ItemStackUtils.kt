@file:Suppress("unused")

package de.maxbossing.mxpaper.items

import com.destroystokyo.paper.profile.ProfileProperty
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.maxbossing.mxpaper.MXColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.collections.ArrayList

/**
 * Converts this string into a list of components, which
 * can be used for minecraft lorelists.
 * @param lineColor The Color of the Line
 * @param lineDecorations The Decorations of the Line
 * @param lineLength The maximum lenght of a Line
 * @return A List of Components
 */
fun String.toLoreList(lineColor: TextColor = de.maxbossing.mxpaper.MXColors.WHITE, vararg lineDecorations: TextDecoration = arrayOf(), lineLength: Int = 40): List<Component> {
    val loreList = ArrayList<Component>()
    val lineBuilder = StringBuilder()

    /**
     * Adds a Line to the Lorelist
     */
    fun submitLine() {
        loreList += Component.text(lineBuilder.toString()).color(lineColor).decorations(lineDecorations.toMutableSet(), true)
        lineBuilder.clear()
    }

    /**
     * Adds a Word to the Line
     */
    fun addWord(word: String) {
        if (lineBuilder.lengthWithoutMinecraftColour + word.lengthWithoutMinecraftColour > lineLength)
            submitLine()

        if (lineBuilder.isNotEmpty())
            lineBuilder.append(" ")

        lineBuilder.append(word)
    }

    split(" ").forEach { addWord(it) }

    if (lineBuilder.isNotEmpty())
        submitLine()

    return loreList
}

/**
 * Returns the length of this sequence, ignoring
 * all minecraft colour codes.
 */
val CharSequence.lengthWithoutMinecraftColour: Int
    get() {
        var count = 0
        var isPreviousColourCode = false

        this.forEachIndexed { index, char ->
            if (isPreviousColourCode) {
                isPreviousColourCode = false
                return@forEachIndexed
            }

            if (char == '§') {
                if (lastIndex >= index + 1) {
                    val nextChar = this[index + 1]
                    if (nextChar.isLetter() || nextChar.isDigit())
                        isPreviousColourCode = true
                    else
                        count++
                }
            } else count++
        }

        return count
    }

/**
 * Creates a Player Head from a Base64 Encoded Skin
 * @param base64String The Base64 Encoded Player Skin
 * @return an Itemstack with the Player Head
 */
@Deprecated("This throws warnings on newer paper versions, Use the SkullMeta Extension function")
fun playerHead(base64String: String): ItemStack {
    val playerHead = ItemStack(Material.PLAYER_HEAD)
    val skullMeta = playerHead.itemMeta as SkullMeta

    val profile = GameProfile(UUID.randomUUID(), UUID.randomUUID().toString())
    profile.properties.put("textures", Property("textures", base64String))

    try {
        val profileField = skullMeta.javaClass.getDeclaredField("profile")
        profileField.isAccessible = true
        profileField.set(skullMeta, profile)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    playerHead.itemMeta = skullMeta
    return playerHead
}

/**
 * Sets the texture of a [SkullMeta] to the given [base64] String
 */
fun SkullMeta.skullTexture(base64: String, uuid: UUID = UUID.randomUUID()): SkullMeta {
    val profile = Bukkit.createProfile(uuid)
    profile.setProperty(ProfileProperty("textures", base64))
    playerProfile = profile
    return this
}