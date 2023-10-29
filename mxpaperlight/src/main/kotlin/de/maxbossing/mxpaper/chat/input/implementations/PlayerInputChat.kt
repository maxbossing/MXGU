package de.maxbossing.mxpaper.chat.input.implementations

import de.maxbossing.mxpaper.chat.input.PlayerInput
import de.maxbossing.mxpaper.chat.input.PlayerInputResult
import de.maxbossing.mxpaper.event.listen
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority

/**
 * A class used to await input from a Player through the Chat
 * @param player The player to await input from
 * @param callback Callback when input is recieved
 * @param timeoutSeconds How long to wait for Input
 * @param question The Question to ask the Player
 * @see de.maxbossing.mxpaper.chat.input.awaitChatInput
 */
internal class PlayerInputChat(
    player: Player,
    callback: (PlayerInputResult<Component>) -> Unit,
    timeoutSeconds: Int,
    question: Component,
) : PlayerInput<Component>(player, callback, timeoutSeconds) {
    init {
        player.sendMessage(question)
    }

    override val inputListeners = listOf(
        listen<AsyncChatEvent>(priority = EventPriority.LOWEST, register = true) {
            if (it.player == player) {
                it.isCancelled = true
                onReceive(it.message())
            }
        }
    )
}
