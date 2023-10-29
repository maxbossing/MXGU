@file:Suppress("MemberVisibilityCanBePrivate")

package de.maxbossing.mxpaper.extensions.kotlin

/**
 * Represents a Pair of two Comparable [T]
 */
internal class MinMaxPair<T : Comparable<T>>(a: T, b: T) {
    /**
     * The smaller of the two values
     */
    val min: T;

    /**
     * The bigger of the two values
     */
    val max: T

    init {
        if (a >= b) {
            min = b; max = a
        } else {
            min = a; max = b
        }
    }
}
