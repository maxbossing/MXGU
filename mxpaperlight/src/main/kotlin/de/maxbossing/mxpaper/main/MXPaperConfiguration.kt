@file:Suppress("unused")
package de.maxbossing.mxpaper.main

import de.maxbossing.mxpaper.MXColors
import net.kyori.adventure.text.format.TextColor
import org.bukkit.event.EventPriority

/**
 * Default Configuration values for MXPaper
 */
object MXPaperConfiguration {
    /**
     * Text Configuration
     */
    object text {
        var baseColor: TextColor = de.maxbossing.mxpaper.MXColors.GRAY
        var errorColor: TextColor = de.maxbossing.mxpaper.MXColors.GRAY
    }

    /**
     * Event Configuration
     */
    object events {
        var eventPriority: EventPriority = EventPriority.NORMAL

        var ignoreCancelled: Boolean = false

        var autoRegistration: Boolean = true
    }

    /**
     * Recipe Configuration
     */
    object recipes {
        var autoRegistration: Boolean = true
    }
}