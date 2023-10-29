@file:Suppress("Unused")

package de.maxbossing.mxpaper.extensions

import de.maxbossing.mxpaper.extensions.bukkit.prefixedmsg
import de.maxbossing.mxpaper.main.PluginInstance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Shortcut to get all online players.
 * @see Bukkit.getOnlinePlayers
 */
val onlinePlayers: Collection<Player> get() = Bukkit.getOnlinePlayers()

/**
 * Shortcut to get a collection of all
 * online players plus the console command sender.
 */
val onlineSenders: Collection<CommandSender> get() = onlinePlayers.plus(Bukkit.getConsoleSender())

/**
 * Shortcut to get the Server.
 * @see Bukkit.getServer
 */
val server get() = Bukkit.getServer()

/**
 * Shortcut to get the PluginManager.
 * @see Bukkit.getPluginManager
 */
val pluginManager get() = Bukkit.getPluginManager()

/**
 * Broadcasts a message ([msg]) on the server.
 * @return the number of recipients
 * @see Bukkit.broadcastMessage
 */
fun broadcast(msg: String) = server.broadcast(text(msg))

/**
 * Broadcasts a message ([msg]) on the server.
 * @return the number of recipients
 * @see Bukkit.broadcastMessage
 */
fun broadcast(msg: Component) = server.broadcast(msg)

/**
 * Shortcut to get the ConsoleSender.
 * @see Bukkit.getConsoleSender
 */
val console get() = Bukkit.getConsoleSender()

/**
 * Shortcut for creating a new [NamespacedKey]
 * @param key The String for the key
 * @return The [NamespacedKey]
 */
fun pluginKey(key: String) = NamespacedKey(PluginInstance, key)

/**
 * Shortcut to get a collection of all worlds
 */
val worlds: List<World> get() = Bukkit.getWorlds()

/**
 * Executes [unit] for all Online Players
 */
fun foreachOnlinePlayers(unit: Player.() -> Unit) {
    onlinePlayers.forEach { it.unit()}
}


/**
 * Translates an [Int] into a roman numeral
 */
val Int.roman: String
    get() {
        val m_k = listOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val m_v = listOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        var str = ""
        var n = this
        for (i in m_k.indices) {
            while (n >= m_k[i]) {
                n -= m_k[i]
                str += m_v[i]
            }
        }
        return str
    }
