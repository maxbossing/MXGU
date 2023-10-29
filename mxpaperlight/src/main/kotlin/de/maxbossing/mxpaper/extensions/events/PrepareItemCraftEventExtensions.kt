package de.maxbossing.mxpaper.extensions.events

import org.bukkit.Material
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack

/**
 * Check if the event is cancelled or cancel it by changing the result to air.
 *
 * If the event is uncancelled later, the original result will be set.
 */
var PrepareItemCraftEvent.isCancelled: Boolean
    get() = inventory.result?.type == Material.AIR
    set(value) { if (value) inventory.result = ItemStack(Material.AIR) else inventory.result = inventory.recipe?.result }
