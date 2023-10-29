package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.extensions.fancy
import org.bukkit.Material
import org.bukkit.inventory.Inventory

/**
 * Closes the inventory for all viewers.
 */
fun Inventory.closeForViewers() = HashSet(viewers).forEach { it.closeInventory() }

/**
 * Checks if an inventory has items
 * @return true if the inventory has items
 */
val Inventory.hasItems: Boolean
    get() {
        for (it in this.contents) {
            if (it != null) return true
        }
        return false
    }

/**
 * Gets the pretty name of a material
 * @return the pretty name
 * @see [fancy]
 */
val Material.prettyName: String
    get() = this.name.fancy