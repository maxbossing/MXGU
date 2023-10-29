package de.maxbossing.mxpaper.extensions.bukkit

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.Directional
import org.bukkit.util.Vector
import kotlin.math.floor

/**
 * @return All blocks in this chunk.
 */
val Chunk.allBlocks
    get() = LinkedHashSet<Block>().apply {
        for (y in world.minHeight until world.maxHeight) {
            for (x in 0 until 16)
                for (z in 0 until 16)
                    add(getBlock(x, y, z))
        }
    }

/**
 * Checks if this Location is in an Area defined by two Locations
 * @param loc1 The first location
 * @param loc2 The second location
 * @return true if the location is in the area
 */
fun Location.inArea(loc1: Location, loc2: Location): Boolean {
    return toVector().inArea(loc1.toVector(), loc2.toVector())
}

/**
 * Checks if this Location is in an Area defined by It's middle and a radius going from there in all directions
 * @param base The middle of the area
 * @param radius The radius from the middle
 * @return true if the location is in the area
 */
fun Location.inArea(base: Location, radius: Double): Boolean {
    return toVector().inArea(base.toVector(), radius)
}

/**
 * Checks if this Vector is in area defined by two Vectors
 * @param loc1 The first location
 * @param loc2 The second location
 * @return true if the location is in the area
 */
fun Vector.inArea(loc1: Vector, loc2: Vector):  Boolean {
    return isInAABB(Vector.getMinimum(loc1, loc2), Vector.getMaximum(loc1, loc2))
}

/**
 * Checks if this Vector is in an Area defined by It's middle and a radius going from there in all directions

 */
fun Vector.inArea(base: Vector, radius: Double): Boolean {
    return isInSphere(base, radius)
}

/**
 * Checks if the given Location is loaded by the Server
 */
val Location.isLoaded : Boolean
    get() {
        if (getWorld() == null) {
            return false
        }
        // Calculate the chunks coordinates. These are 1,2,3 for each chunk, NOT
        // location rounded to the nearest 16.
        // Calculate the chunks coordinates. These are 1,2,3 for each chunk, NOT
        // location rounded to the nearest 16.
        val x = floor(getBlockX() / 16.0).toInt()
        val z = floor(getBlockZ() / 16.0).toInt()
        return if (getWorld().isChunkLoaded(x, z)) {
            true
        } else {
            false
        }
    }

/**
 * Return The Block a Sign is attached to
 *
 * Will be null If the Block is not a sign or the Sign ha no attached Block
 */
val Block.attachedBlock: Block?
    get() {
        return try {
            if (blockData is Directional) {
                val directional: Directional = blockData as Directional
                getRelative(directional.getFacing().getOppositeFace())
            } else {
                null
            }
            // sometimes??
        } catch (e: NullPointerException) {
            null // /Not sure what causes this.
        } catch (e: ClassCastException) {
            null
        }
    }