package de.maxbossing.mxpaper.chat.input.implementations

import de.maxbossing.mxpaper.chat.input.PlayerInput
import de.maxbossing.mxpaper.chat.input.PlayerInputResult
import de.maxbossing.mxpaper.event.listen
import de.maxbossing.mxpaper.extensions.bukkit.content
import de.maxbossing.mxpaper.items.itemStack
import de.maxbossing.mxpaper.items.meta
import de.maxbossing.mxpaper.main.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType

/**
 * Class to query input from a Player using a Book
 * Pages will be flattened and the Input will be returned as one continous string
 * @param player the Player to await Input from
 * @param callback Callback when input is recieved
 * @param timeoutSeconds How long to wait for input
 * @see de.maxbossing.mxpaper.chat.input.awaitBookInputAsString
 */
internal class PlayerInputBookComprehensive(
    player: Player,
    callback: (PlayerInputResult<String>) -> Unit,
    timeoutSeconds: Int,
) : PlayerInputBook<String>(player, callback, timeoutSeconds) {
    override fun loadBookContent(bookMeta: BookMeta) = bookMeta.content
}

/**
 * Class to query Input from a Player using a Book. Every Page is a String in the List, sorted by the Page number
 * @param player the Player to await input from
 * @param callback Callback when Input is recieved
 * @param timeoutSeconds How long to wait for Input
 * @see de.maxbossing.mxpaper.chat.input.awaitBookInputAsList
 */
internal class PlayerInputBookPaged(
    player: Player,
    callback: (PlayerInputResult<List<Component>>) -> Unit,
    timeoutSeconds: Int,
) : PlayerInputBook<List<Component>>(player, callback, timeoutSeconds) {
    override fun loadBookContent(bookMeta: BookMeta): List<Component> = bookMeta.pages()
}

/**
 * Class used to query Input from a Player using a Book
 * @param player the player to await input from
 * @param callback Callback when Input is recieved
 * @param timeoutSeconds How long to wait for Input
 */
internal abstract class PlayerInputBook<T>(
    player: Player,
    callback: (PlayerInputResult<T>) -> Unit,
    timeoutSeconds: Int,
) : PlayerInput<T>(player, callback, timeoutSeconds) {
    private val id = getID()

    val bookItemStack = itemStack(Material.WRITABLE_BOOK) {
        meta {
            persistentDataContainer[idKey, PersistentDataType.INTEGER] = id
        }
    }

    init {
        player.inventory.addItem(bookItemStack)
    }

    abstract fun loadBookContent(bookMeta: BookMeta): T

    override val inputListeners = listOf(
        listen<PlayerEditBookEvent> {
            val meta = it.newBookMeta
            if (meta.persistentDataContainer[idKey, PersistentDataType.INTEGER] == id) {
                onReceive(loadBookContent(meta))
                usedIDs -= id
                it.isCancelled = true
                player.inventory.removeItem(bookItemStack)
            }
        }
    )

    override fun onTimeout() {
        player.closeInventory()
        usedIDs -= id
    }

    companion object {
        val idKey = NamespacedKey(PluginInstance, "kspigot_bookinput_id")

        internal val usedIDs = ArrayList<Int>()

        fun getID(): Int {
            var returnID = (0..Int.MAX_VALUE).random()
            while (usedIDs.contains(returnID)) returnID = (0..Int.MAX_VALUE).random()
            return returnID
        }
    }
}