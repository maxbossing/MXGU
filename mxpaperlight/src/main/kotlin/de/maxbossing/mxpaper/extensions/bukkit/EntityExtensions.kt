@file:Suppress("Unused")

package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.extensions.onlinePlayers
import de.maxbossing.mxpaper.main.PluginInstance
import de.maxbossing.mxpaper.pluginmessages.PluginMessageConnect
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.sqrt


/**
 * Checks if the entities' head is in water.
 */
val LivingEntity.isHeadInWater: Boolean get() = this.eyeLocation.block.type == Material.WATER

/**
 * Checks if the entities' feet are in water.
 */
val Entity.isFeetInWater: Boolean get() = this.location.block.type == Material.WATER

/**
 * Checks if the entity stands on solid ground.
 */
val Entity.isGroundSolid: Boolean get() = this.location.add(0.0, -0.01, 0.0).block.type.isSolid

/**
 * Returns the material that is present under the feet of this entity.
 */
val Entity.groundMaterial get() = this.location.add(0.0, -0.01, 0.0).block.type

/**
 * Checks if an [Entity] is standing on a Block
 * The method from bukkit is deprecated because it can be spoofed by the client.
 * This can't be spoofed.
 */
val Entity.isStandingOnBlock: Boolean
    get() = groundMaterial.isSolid

/**
 *  Checks if the entity is standing in mid air.
 */
val Entity.isStandingInMidAir: Boolean
    get() = !isStandingOnBlock && vehicle == null && !location.clone().add(0.0, 0.1, 0.0).block.type.isSolid && !location.block.type.isSolid

/**
 * @return The max health of the entity
 * @throws NullPointerException if the entity doesn't have a max health value
 */
val LivingEntity.realMaxHealth: Double
    get() = getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
        ?: throw NullPointerException("The entity does not have a max health value!")

/**
 * Kills the damageable.
 */
fun Damageable.kill() {
    health = 0.0
}

/**
 * Sets the entities' health to the max possible value.
 * @throws NullPointerException if the entity does not have a max health value
 */
fun LivingEntity.heal() {
    health = getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
        ?: throw NullPointerException("The entity does not have a max health value!")
}

/**
 * Sets the players' saturation to the
 * current max possible value.
 */
fun Player.saturate() {
    saturation = foodLevel.toFloat()
}

/**
 * Hides the player for all [onlinePlayers].
 */
fun Player.hideSelf() {
    onlinePlayers.filter { it != this }.forEach { it.hidePlayer(PluginInstance, this) }
}

/**
 * Shows the player for all [onlinePlayers].
 */
fun Player.showSelf() {
    onlinePlayers.filter { it != this }.forEach { it.showPlayer(PluginInstance, this) }
}

/**
 * Hides all online players from this player.
 */
fun Player.hideEveryone() {
    onlinePlayers.filter { it != this }.forEach { this.hidePlayer(PluginInstance, it) }
}

/**
 * Shows all online players to this player.
 */
fun Player.showEveryone() {
    onlinePlayers.filter { it != this }.forEach { this.showPlayer(PluginInstance, it) }
}
/**
 * Returns the itemInHand of the given [EquipmentSlot]
 * if it is an hand slot.
 * @param hand The [EquipmentSlot] to check
 */
fun Player.getHandItem(hand: EquipmentSlot) = when (hand) {
    EquipmentSlot.HAND -> inventory.itemInMainHand
    EquipmentSlot.OFF_HAND -> inventory.itemInOffHand
    else -> null
}

/**
 * Sends the player to the given server in the
 * BungeeCord network.
 */
fun Player.sendToServer(servername: String) {
    PluginMessageConnect(servername).sendWithPlayer(this)
}

/**
 * Adds the given ItemStacks to the player's inventory.
 * @param itemStacks The ItemStacks to give
 * @return The items that did not fit into the player's inventory.
 */
fun Player.give(vararg itemStacks: ItemStack) = inventory.addItem(*itemStacks)

/**
 * Adds all equipment locks to every equipment slot
 */
fun ArmorStand.fullLock() {
    for (slot in EquipmentSlot.values()) {
        lock(slot)
    }
}

/**
 * Adds all equipment locks to the given slot
 * @param slot the slot which gets locked
 */
fun ArmorStand.lock(slot: EquipmentSlot) {
    for (lock in ArmorStand.LockType.values()) {
        addEquipmentLock(slot, lock)
    }
}

/**
 * Removes all Equipment locks from all equipment slots
 */
fun ArmorStand.fullUnlock() {
    for (slot in EquipmentSlot.values()) {
        unlock(slot)
    }
}

/**
 * Removes all equipment slots from the given slot
 * @param slot the slot to unlock
 */
fun ArmorStand.unlock(slot: EquipmentSlot) {
    for (lock in ArmorStand.LockType.values()) {
        removeEquipmentLock(slot, lock)
    }
}

/**
 * Boosts the entity by the given height.
 * @param height the height in blocks to boost the entity by
 */
fun Entity.boost(height: Int) {
    val velocity = this.velocity
    velocity.y = sqrt(2 * height * 9.81) * 0.08
    this.velocity = velocity
}


/**
 * Check if the player is within given radius of the given location
 * @param location1 the location to check
 * @param radius the radius to check
 * @return true if the player is in the radius
 */
fun Player.isInArea(location1: Location, radius: Double): Boolean {
    return location.toVector().isInSphere(location1.toVector(), radius)
}

/**
 * Checks if a player is in a cube defined by two edges
 * @param location1 the first edge
 * @param location2 the second edge (must be the opposite of the first edge)
 * @return true if the player is in the cube
 */
fun Player.isInArea(location1: Location, location2: Location): Boolean {
    val vector1 = location1.toVector()
    val vector2 = location2.toVector()
    return location.toVector().isInAABB(Vector.getMinimum(vector1, vector2), Vector.getMaximum(vector1, vector2))
}

/**
 * Get playerdata File of a player
 * @return playerdata file, if the file not exist, it will return null.
 */
fun OfflinePlayer.getDataFile(): File? {
    return getPlayerDataFile(uniqueId)
}

/**
 * Get playerdata File of a player
 *
 * @param playerUUID The player's uuid
 * @return playerdata file, if the file not exist, it will return null.
 */
fun getPlayerDataFile(uuid: UUID): File? {
    if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) return null
    var playerdataFile = File(Bukkit.getWorldContainer(), Bukkit.getWorlds()[0].name)
    playerdataFile = File(playerdataFile, "playerdata")
    if (!playerdataFile.exists()) return null
    playerdataFile = File(playerdataFile, "${uuid}.dat")
    return if (!playerdataFile.exists()) null else playerdataFile
}