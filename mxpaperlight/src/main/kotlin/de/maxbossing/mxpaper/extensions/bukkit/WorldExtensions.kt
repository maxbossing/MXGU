package de.maxbossing.mxpaper.extensions.bukkit

import de.maxbossing.mxpaper.main.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

/**
 * Returns all Blocks in a chunk
 */
val Chunk.blocks: Set<Block>
    get() = LinkedHashSet<Block>().apply {
        for (y in world.minHeight until world.maxHeight) {
            for (x in 0 until 16)
                for (z in 0 until 16)
                    add(getBlock(x, y, z))
        }
    }

/**
 * Returns the bounding box of a Chunk
 *
 * The Bounding box are the locations that face the outside of the chunk
 *
 * @return a Set of Locations
 */
val Chunk.boundingBox: Set<Location>
    get() {
        val chunk: Chunk = this

        val chunkX = chunk.x
        val chunkZ = chunk.z
        val minBlockX = chunkX shl 4
        val minBlockZ = chunkZ shl 4
        val maxBlockX = minBlockX + 15
        val maxBlockZ = minBlockZ + 15

        val set = hashSetOf<Location>()

        var y = 0
        while (y < world.maxHeight) {
            val minBlockLocation =
                Location(world, minBlockX.toDouble(), y.toDouble(), minBlockZ.toDouble())
            val maxBlockLocation =
                Location(world, maxBlockX.toDouble(), (y + 15).toDouble(), maxBlockZ.toDouble())
            set.add(minBlockLocation)
            set.add(maxBlockLocation)
            y += 16

        }
        return set
    }


/**
 * Deletes the world and kicks all players with the given reason.
 * @param reason the reason to kick the players with
 * @return true if the world was deleted successfully, false if an error occurred
 */
fun World.delete(reason: Component): Boolean {
    if (this.players.isNotEmpty()) {
        this.players.forEach {
            it.kick(reason)
        }
    }

    if (!PluginInstance.server.unloadWorld(this, true)) return false

    return this.worldFolder.deleteRecursively()
}