@file:Suppress("unused")
package de.maxbossing.mxpaper.extensions.events

import de.maxbossing.mxpaper.extensions.pluginManager
import de.maxbossing.mxpaper.main.PluginInstance
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

/**
 * Registers a [Listener] with the [PluginInstance]
 */
fun Listener.register() {
    pluginManager.registerEvents(this, PluginInstance)
}

/**
 * Unregisters a Listener
 */
fun Listener.unregister() {
    HandlerList.unregisterAll(this)
}