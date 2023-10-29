package de.maxbossing.mxpaper.gui.elements

import de.maxbossing.mxpaper.gui.ForInventory
import de.maxbossing.mxpaper.gui.GUIClickEvent
import de.maxbossing.mxpaper.gui.GUIElement
import org.bukkit.inventory.ItemStack

class GUIPlaceholder<T : ForInventory>(
    private val icon: ItemStack,
) : GUIElement<T>() {
    override fun getItemStack(slot: Int) = icon
    override fun onClickElement(clickEvent: GUIClickEvent<T>) {
        clickEvent.bukkitEvent.isCancelled = true
    }
}
