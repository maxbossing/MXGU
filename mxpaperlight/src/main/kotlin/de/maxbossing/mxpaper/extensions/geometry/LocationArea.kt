@file:Suppress("MemberVisibilityCanBePrivate")

package de.maxbossing.mxpaper.extensions.geometry

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import kotlin.math.max
import kotlin.math.min

/**
 * Represents a Pair of locations
 */
class SimpleLocationPair(loc1: Location, loc2: Location) {
    /**
     * The world in which the Locations are
     * @throws IllegalArgumentException if the Locations are not in the same world
     * @throws IllegalArgumentException If the world is not loaded
     */
    val world = loc1.worldOrNull.let {
        if (it == loc2.worldOrNull) it
        else throw IllegalArgumentException("The given locations worlds are not the same!")
    }?: throw IllegalArgumentException("The given world is not loaded")

    /**
     * The minimum X,Y,Z of the Pair
     */
    val minSimpleLoc = SimpleLocation3D(min(loc1.x, loc2.x), min(loc1.y, loc2.y), min(loc1.z, loc2.z))

    /**
     * The maximum X,Y,Z of the Pair
     */
    val maxSimpleLoc = SimpleLocation3D(max(loc1.x, loc2.x), max(loc1.y, loc2.y), max(loc1.z, loc2.z))

    /**
     * Checks if the given Location is in the Area of the Pair
     * @param loc the Location to check
     * @param check3d Whether to check in 3D
     * @param tolerance The tolerance of the Result
     * @return True if the location is in the Area
     */
    fun isInArea(
        loc: Location,
        check3d: Boolean = true,
        tolerance: Int = 0,
    ): Boolean {
        // checking world
        if (loc.world != world) return false

        return if (
        // checking x
            loc.x >= minSimpleLoc.x - tolerance && loc.x <= maxSimpleLoc.x + tolerance &&
            // checking z
            loc.z >= minSimpleLoc.z - tolerance && loc.z <= maxSimpleLoc.z + tolerance
        ) {
            // checking y
            if (check3d) loc.y >= minSimpleLoc.y - tolerance && loc.y <= maxSimpleLoc.y + tolerance else true
        } else false
    }

    /**
     * All Chunks the Location touches
     */
    val touchedSimpleChunks: Set<SimpleChunkLocation> by lazy {
        val foundChunks = HashSet<SimpleChunkLocation>()

        (minSimpleLoc.chunk.x until maxSimpleLoc.chunk.x + 1).forEach { curX ->
            (minSimpleLoc.chunk.z until maxSimpleLoc.chunk.z + 1).forEach { curZ ->
                foundChunks += SimpleChunkLocation(curX, curZ)
            }
        }

        return@lazy foundChunks
    }
}

/**
 * Represents a Pair of Locations
 */
class LocationArea(loc1: Location, loc2: Location) {
    /**
     * The first Location
     */
    var loc1: Location = loc1
        set(value) {
            field = value
            simpleLocationPair = SimpleLocationPair(value, loc2)
        }

    /**
     * The second Location
     */
    var loc2: Location = loc2
        set(value) {
            field = value
            simpleLocationPair = SimpleLocationPair(loc1, value)
        }

    /**
     * The locations as [SimpleLocationPair]
     */
    var simpleLocationPair = SimpleLocationPair(loc1, loc2); private set

    /**
     * The world in which the Locations are
     */
    val world: World get() = simpleLocationPair.world

    /**
     * The minimum Location of the Pair
     */
    val minLoc: Location get() = simpleLocationPair.minSimpleLoc.withWorld(simpleLocationPair.world)

    /**
     * The maximum Location of the Pair
     */
    val maxLoc: Location get() = simpleLocationPair.maxSimpleLoc.withWorld(simpleLocationPair.world)

    /**
     * All Chunks touched by the Pair
     */
    val touchedChunks: Set<Chunk> get() = simpleLocationPair.touchedSimpleChunks.mapTo(HashSet()) { it.withWorld(world) }

    /**
     * Checks whether a Location is in the Pair
     * @param loc The Location to check
     * @param check3d Whether to check in 3-Dimensional space
     * @param tolerance The tolerance for the result
     * @return true if the Location is in the pair
     */
    fun isInArea(
        loc: Location,
        check3d: Boolean = true,
        tolerance: Int = 0,
    ) = simpleLocationPair.isInArea(loc, check3d, tolerance)
}
