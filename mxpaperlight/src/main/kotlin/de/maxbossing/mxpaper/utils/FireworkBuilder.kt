package de.maxbossing.mxpaper.utils

import de.maxbossing.mxpaper.items.meta
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Firework
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta

/**
 * Opens a new FireworkMeta builder and creates
 * an ItemStack with the new FireworkMeta applied.
 */
fun fireworkItemStack(amount: Int = 1, builder: FireworkMeta.() -> Unit) =
    ItemStack(Material.FIREWORK_ROCKET, amount).apply {
        meta<FireworkMeta> {
            builder.invoke(this)
        }
    }

/**
 * Opens a new effect builder and adds this
 * new effect to this FireworkMeta.
 */
fun FireworkMeta.addEffect(builder: FireworkEffect.Builder.() -> Unit) =
    addEffect(FireworkEffect.builder().apply(builder).build())

/**
 * Lets you edit the meta of this Firework entity.
 * The builder is automatically applied.
 */
fun Firework.editMeta(builder: FireworkMeta.() -> Unit) {
    fireworkMeta = fireworkMeta.apply(builder)
}
