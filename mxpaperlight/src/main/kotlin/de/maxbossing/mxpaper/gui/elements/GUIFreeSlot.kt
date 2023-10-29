package de.maxbossing.mxpaper.gui.elements

import de.maxbossing.mxpaper.gui.ForInventory
import de.maxbossing.mxpaper.gui.GUIClickEvent
import de.maxbossing.mxpaper.gui.GUISlot

class GUIFreeSlot<T : ForInventory> : GUISlot<T>() {
    override fun onClick(clickEvent: GUIClickEvent<T>) {
        /* do nothing */
    }
}
