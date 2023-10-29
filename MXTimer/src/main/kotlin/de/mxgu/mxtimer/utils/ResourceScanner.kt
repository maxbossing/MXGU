package de.mxgu.mxtimer.utils

import java.io.File
import java.net.URL

fun getResourceFolderFiles(folder: String): Array<File> {
    val loader = Thread.currentThread().getContextClassLoader()
    val url: URL = loader.getResource(folder)
    val path: String = url.getPath()
    return File(path).listFiles()
}