package de.maxbossing.mxpaper.extensions.kotlin

import java.io.File

/**
 * Creates a [File] if it doesn't exist
 */
fun File.createIfNotExists(): Boolean {
    return if (!exists()) {
        if (!parentFile.exists())
            parentFile.mkdirs()
        if (isDirectory)
            mkdir()
        else createNewFile()
    } else true
}
