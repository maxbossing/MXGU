@file:Suppress("unused")
package de.maxbossing.mxpaper.main

import de.maxbossing.mxpaper.MXColors
import de.maxbossing.mxpaper.extensions.bukkit.cmp
import de.maxbossing.mxpaper.extensions.bukkit.plus
import de.maxbossing.mxpaper.extensions.kotlin.closeIfInitialized
import de.maxbossing.mxpaper.extensions.pluginManager
import de.maxbossing.mxpaper.gui.GUIHolder
import de.maxbossing.mxpaper.runnables.MXRunnableHolder
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main plugin instance. Available with public visibility.
 */
val MXPaperMainInstance: MXPaper get() = PluginInstance

/**
 * The Main Plugin Prefix. Available with public visibility
 */
lateinit var prefix: Component

/**
 * The main plugin instance. Less complicated name for internal usage.
 */
@PublishedApi
internal lateinit var PluginInstance: MXPaper
    private set

/**
 * This is the main instance of KSpigot.
 *
 * This class replaces (and inherits from) the
 * JavaPlugin class. Your main plugin class should
 * inherit from this abstract class.
 *
 * **Instead** of overriding [onLoad()], [onEnable()]
 * and [onDisable()] **override**:
 * - [load()] (called first)
 * - [startup()]  (called second)
 * - [shutdown()] (called in the "end")
 */
abstract class MXPaper : JavaPlugin() {
    // lazy properties
    private val kRunnableHolderProperty = lazy { MXRunnableHolder }
    private val guiHolderProperty = lazy { GUIHolder }
    internal val kRunnableHolder by kRunnableHolderProperty
    internal val guiHolder by guiHolderProperty

    /**
     * Called when the plugin was loaded
     */
    open fun load() {}

    /**
     * Called when the plugin was enabled
     */
    open fun startup() {}

    /**
     * Called when the plugin gets disabled
     */
    open fun shutdown() {}

    final override fun onLoad() {
        if (::PluginInstance.isInitialized) {
            server.logger.warning("The main instance of MXPaper has been modified, even though it has already been set by another plugin!")
        }
        PluginInstance = this
        load()
    }

    final override fun onEnable() {
        startup()

        if (!::prefix.isInitialized) {
            if (pluginMeta.loggerPrefix != null) {
                prefix = cmp("${pluginMeta.loggerPrefix}", color = de.maxbossing.mxpaper.MXColors.BLUEVIOLET, bold = true) + cmp(" » ", color = de.maxbossing.mxpaper.MXColors.GRAY)
            }
            else {
                logger.severe("The Plugin forgot to define a prefix! please set the prefix variable in your Main Class!")
                pluginManager.disablePlugin(this)
            }
        }
    }

    final override fun onDisable() {
        shutdown()
        // avoid unnecessary load of lazy properties
        kRunnableHolderProperty.closeIfInitialized()
        guiHolderProperty.closeIfInitialized()
    }
}
