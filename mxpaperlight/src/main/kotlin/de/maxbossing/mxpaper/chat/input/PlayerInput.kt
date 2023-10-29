@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package de.maxbossing.mxpaper.chat.input

import de.maxbossing.mxpaper.chat.input.implementations.PlayerInputBookComprehensive
import de.maxbossing.mxpaper.chat.input.implementations.PlayerInputBookPaged
import de.maxbossing.mxpaper.chat.input.implementations.PlayerInputChat
import de.maxbossing.mxpaper.event.unregister
import de.maxbossing.mxpaper.runnables.sync
import de.maxbossing.mxpaper.runnables.taskRunLater
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.entity.Player
import org.bukkit.event.Listener

/**
 * Asks the player a question and uses the next
 * chat input of the player as his input.
 * @param question The Question to ask the player
 * @param timeoutSeconds How long to wait for user input
 * @param callback Callback when the Player answered the Question
 */
fun Player.awaitChatInput(
    question: Component = text("Type your input in the chat!"),
    timeoutSeconds: Int = 1 * 60,
    callback: (PlayerInputResult<Component>) -> Unit,
) {
    PlayerInputChat(this, callback, timeoutSeconds, question)
}

/**
 * Opens a book and uses the text the player inserted on all sites as the players' input.
 * In this case, all pages will be flattened to a single string.
 * @param timeoutSeconds How long to wait for user input
 * @param callback Callback when the Player closes the book
 */
fun Player.awaitBookInputAsString(
    timeoutSeconds: Int = 1 * 60,
    callback: (PlayerInputResult<String>) -> Unit,
) = PlayerInputBookComprehensive(this, callback, timeoutSeconds).bookItemStack

/**
 * Opens a book and uses the text the player inserted
 * on all sites as the players' input.
 * In this case, every page is represented by one string
 * element in a list of strings.
 * @param timeoutSeconds How long to wait for user input
 * @param callback Callback when the Player closes the Book
 */
fun Player.awaitBookInputAsList(
    timeoutSeconds: Int = 1 * 60,
    callback: (PlayerInputResult<List<Component>>) -> Unit,
) = PlayerInputBookPaged(this, callback, timeoutSeconds).bookItemStack


/**
 * Represents the Input a player has made through a [PlayerInput]
 * @param input The input the player gave. Null on timeout or invalid input.
 */
class PlayerInputResult<T> internal constructor(val input: T?)

/**
 * A class representing a way to await Input from a Player
 * @param player The player to await input from
 * @param callback Callback when input is recieved
 * @param timeoutSeconds How long to wait for Input
 */
internal abstract class PlayerInput<T>(
    protected val player: Player,
    private val callback: (PlayerInputResult<T>) -> Unit,
    timeoutSeconds: Int,
) {
    private var received = false

    protected abstract val inputListeners: List<Listener>

    protected fun onReceive(input: T?) {
        if (!received) {
            inputListeners.forEach { it.unregister() }
            received = true
            sync {
                callback.invoke(PlayerInputResult(input))
            }
        }
    }

    open fun onTimeout() {}

    init {
        taskRunLater(delay = (20 * timeoutSeconds).toLong()) {
            if (!received) onTimeout()
            onReceive(null)
        }
    }
}
