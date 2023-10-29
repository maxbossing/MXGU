@file:Suppress("unused")

package de.maxbossing.mxpaper.extensions.geometry

import org.bukkit.Location
import org.bukkit.util.Vector

/*
 * LOCATION
 */

// INCREASE
// all
/**
 * Increases all coordinates of the Location by a number
 * @param distance The Distance to increase
 */
infix fun Location.increase(distance: Number) = add(distance, distance, distance)

// single
/**
 * Increases the X coordinate of a Location by a number
 * @param distance The distance to increase
 */
infix fun Location.increaseX(distance: Number) = add(distance, 0.0, 0.0)

/**
 * Increases the Y coordinate of a Location by a number
 * @param distance the Distance to increase
 */
infix fun Location.increaseY(distance: Number) = add(0.0, distance, 0.0)

/**
 * Increases the Z coordinate of a Location by a number
 * @param distance The Distance to increase
 */
infix fun Location.increaseZ(distance: Number) = add(0.0, 0.0, distance)

// pair
/**
 * Increases the X and Y coordinate of a Location by a number
 * @param distance The Distance to increase
 */
infix fun Location.increaseXY(distance: Number) = add(distance, distance, 0.0)

/**
 * Increases the Y and Z coordinate of a Location by a number
 * @param distance The Distance to increase
 */
infix fun Location.increaseYZ(distance: Number) = add(0.0, distance, distance)

/**
 * Increases the X and Z coordinate of a Location by a number
 * @param distance The Distance to increase
 */
infix fun Location.increaseXZ(distance: Number) = add(distance, 0.0, distance)

// REDUCE
// all
/**
 * Decreases all Coordinates of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduce(distance: Number) = subtract(distance, distance, distance)

// single
/**
 * Decreases the X Coordinate of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceX(distance: Number) = subtract(distance, 0.0, 0.0)

/**
 * Decreaes the Y Coordinate of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceY(distance: Number) = subtract(0.0, distance, 0.0)

/**
 * Decreases the Z coordinate of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceZ(distance: Number) = subtract(0.0, 0.0, distance)

// pair
/**
 * Decreases the X and Y Coordinate of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceXY(distance: Number) = subtract(distance, distance, 0.0)

/**
 * Decreases the Y and Z Coordinate of a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceYZ(distance: Number) = subtract(0.0, distance, distance)

/**
 * Decreases the X and Z Coordinate fo a Location by a number
 * @param distance The Distance to decrease
 */
infix fun Location.reduceXZ(distance: Number) = subtract(distance, 0.0, distance)

// extensions

/**
 * Adds to the Coordinates of a Location
 * @param x The amount to add to the X coordinate
 * @param y The amount to add to the Y coordinate
 * @param z The amount to add to the Z coordinate
 * @return The Location with the numbers added
 */
fun Location.add(x: Number, y: Number, z: Number) = add(x.toDouble(), y.toDouble(), z.toDouble())

/**
 * Subtracts from the Coordinates of a Location
 * @param x The amount to subtract from the X coordinate
 * @param y The amount to subtract from the Y coordinate
 * @param z The amount to subtract from the Z coordinate
 * @return The Location with the nunbers subtracted
 */
fun Location.subtract(x: Number, y: Number, z: Number) = subtract(x.toDouble(), y.toDouble(), z.toDouble())

/**
 * Returns the Block Location
 */
val Location.blockLoc: Location get() = Location(world, blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())

/**
 * Calculates the Relation to another Location
 * @param loc The Location to calculate the relation from
 * @return The Relation of the Two Locations
 */
infix fun Location.relationTo(loc: Location) = this.subtract(loc).toSimple()

// operator functions
// immutable

/**
 * Adds a [Vector] to a [Location]
 * @param vec The Vector to add
 * @return The Added Location
 */
operator fun Location.plus(vec: Vector) = clone().add(vec)

/**
 * Subtracts a [Vector] from a [Location]
 * @param vec The Vector to subtract
 * @return The Subtracted Location
 */
operator fun Location.minus(vec: Vector) = clone().subtract(vec)

/**
 * Adds a [Location] to a [Location]
 * @param loc The Location to add
 * @return The added Location
 */
operator fun Location.plus(loc: Location) = clone().add(loc)

/**
 * Subtracts a [Location] from a [Location]
 * @param loc The Location to subtract
 * @return The subtracted Location
 */
operator fun Location.minus(loc: Location) = clone().subtract(loc)

/**
 * Adds a [SimpleLocation3D] to a [Location]
 * @param loc The Location to add
 * @return The added Location
 */
operator fun Location.plus(loc: SimpleLocation3D) = clone().add(loc.x, loc.y, loc.z)

/**
 * Subtracts a [SimpleLocation3D] from a [Location]
 * @param loc The Location to subtract
 * @return The subtracted Location
 */
operator fun Location.minus(loc: SimpleLocation3D) = clone().subtract(loc.x, loc.y, loc.z)

// mutable
operator fun Location.plusAssign(vec: Vector) {
    add(vec)
}


operator fun Location.minusAssign(vec: Vector) {
    subtract(vec)
}


operator fun Location.plusAssign(loc: Location) {
    add(loc)
}

operator fun Location.minusAssign(loc: Location) {
    subtract(loc)
}

operator fun Location.plusAssign(loc: SimpleLocation3D) {
    add(loc.x, loc.y, loc.z)
}

operator fun Location.minusAssign(loc: SimpleLocation3D) {
    subtract(loc.x, loc.y, loc.z)
}

// mutable with return

infix fun Location.increase(vec: Vector) = add(vec)

infix fun Location.reduce(vec: Vector) = subtract(vec)

infix fun Location.increase(loc: Location) = add(loc)

infix fun Location.reduce(loc: Location) = subtract(loc)

infix fun Location.increase(loc: SimpleLocation3D) = add(loc.x, loc.y, loc.z)

infix fun Location.reduce(loc: SimpleLocation3D) = subtract(loc.x, loc.y, loc.z)

/*
 * VECTOR
 */
/**
 * Checks if a [Vector] is finite
 */
val Vector.isFinite: Boolean
    get() = x.isFinite() && y.isFinite() && z.isFinite()

// fast construct
/**
 * Creates a [Vector]
 * @param x The X value of the Vector
 * @param Y The Y value of the Vector
 * @param Z The Z value of the Vector
 * @return A Vector with given Parameters
 */
fun vec(x: Number = 0.0, y: Number = 0.0, z: Number = 0.0) = Vector(x.toDouble(), y.toDouble(), z.toDouble())

/**
 * Creates a [Vector] by X and Y
 * @param x The X value
 * @param y The Y value
 * @return A Vector with given values
 */
fun vecXY(x: Number, y: Number) = vec(x, y)

/**
 * Creates a [Vector] by X and Z
 * @param x The X Value
 * @param y The Y Value
 * @return A Vector with given Values
 */
fun vecXZ(x: Number, z: Number) = vec(x, z = z)

/**
 * Creates a [Vector] by Y and Z
 * @param y The Y Value
 * @param z The Z Value
 * @return A Vector with given Values
 */
fun vecYZ(y: Number, z: Number) = vec(y = y, z = z)

/**
 * Creates a [Vector] by X
 * @param x The X Value
 * @return A Vector with given Values
 */
fun vecX(x: Number) = vec(x)

/**
 * Creates a [Vector] by Y
 * @param y The Y Value
 * @return A Vector with given Values
 */
fun vecY(y: Number) = vec(y = y)

/**
 * Creates a [Vector] by Z
 * @param z The Z Value
 * @return A Vector with given Value
 */
fun vecZ(z: Number) = vec(z = z)

// operator functions
// immutable
/**
 * Adds a [Vector] to Another
 */
operator fun Vector.plus(vec: Vector) = clone().add(vec)

/**
 * Subtracts a [Vector] from Another
 */
operator fun Vector.minus(vec: Vector) = clone().subtract(vec)

/**
 * Multiplies a [Vector] by Another
 */
operator fun Vector.times(vec: Vector) = clone().multiply(vec)

/**
 * Multiplies a [Vector]
 */
operator fun Vector.times(num: Number) = clone().multiply(num.toDouble())

// mutable

operator fun Vector.plusAssign(vec: Vector) {
    add(vec)
}

operator fun Vector.minusAssign(vec: Vector) {
    subtract(vec)
}

operator fun Vector.timesAssign(vec: Vector) {
    multiply(vec)
}

operator fun Vector.timesAssign(num: Number) {
    multiply(num.toDouble())
}

// mutable with return
/**
 * Increases a [Vector] by Another
 */
infix fun Vector.increase(vec: Vector) = add(vec)

/**
 * Decreases a [Vector] by another
 */
infix fun Vector.reduce(vec: Vector) = subtract(vec)

/**
 * Multiplies a [Vector] by another
 */
infix fun Vector.multiply(vec: Vector) = multiply(vec)

/**
 * Multiplies a [Vector]
 */
infix fun Vector.multiply(num: Number) = multiply(num.toDouble())