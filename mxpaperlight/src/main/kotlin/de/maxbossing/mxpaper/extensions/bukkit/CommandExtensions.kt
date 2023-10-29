package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.main.PluginInstance
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter

/**
 * Registers this CommandExecutor for
 * the given command.
 * @param commandName The command to register the CommandExecutor for
 * @return If the command was registered successfully.
 */
fun CommandExecutor.register(commandName: String): Boolean {
    PluginInstance.getCommand(commandName)?.let {
        it.setExecutor(this)
        if (this is TabCompleter)
            it.tabCompleter = this
        return true
    }
    return false
}
