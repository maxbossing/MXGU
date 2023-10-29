package de.maxbossing.mxpaper.gui.elements

import de.maxbossing.mxpaper.gui.ForInventory
import de.maxbossing.mxpaper.gui.GUIClickEvent
import de.maxbossing.mxpaper.gui.GUIElement
import org.bukkit.inventory.ItemStack

open class GUIButton<T : ForInventory>(
    private val icon: ItemStack,
    private val action: (GUIClickEvent<T>) -> Unit,
) : GUIElement<T>() {
    final override fun getItemStack(slot: Int) = icon
    override fun onClickElement(clickEvent: GUIClickEvent<T>) {
        clickEvent.bukkitEvent.isCancelled = true
        action(clickEvent)
    }
}
