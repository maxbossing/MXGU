package de.maxbossing.mxpaper.extensions.kotlin

inline fun <T, R> Lazy<T>.ifInitialized(block: (T) -> R) = if (isInitialized()) block(value) else null

/**
 * Returns the value of a [Lazy] if the [Lazy] is initialized
 */
val <T> Lazy<T>.valueIfInitialized get() = ifInitialized { value }

/**
 * Closes a [Lazy]<[AutoCloseable]> if the [Lazy] is initialized
 */
fun Lazy<AutoCloseable>.closeIfInitialized() = ifInitialized { value.close() }
