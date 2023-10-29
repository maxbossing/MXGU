package de.maxbossing.mxpaper.extensions.kotlin

/**
 * Applies [block] to [T] only if [block] is not null
 * @param block The block to apply
 * @return The [T] with the block applied if [block] is not null
 */
internal fun <T> T.applyIfNotNull(block: (T.() -> Unit)?): T {
    if (block != null)
        apply(block)
    return this
}
