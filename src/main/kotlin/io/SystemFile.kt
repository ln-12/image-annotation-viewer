package io

import kotlinx.coroutines.CoroutineScope
import ui.util.TextLines

fun createSystemFile(roots: Array<java.io.File>): File = object : File {
    // this fake file represents a virtual root for all drives on Windows
    override val name = "System"
    override val path = roots[0].toPath()
    override val isDirectory = true
    override val children = roots.map { it.toProjectFile() }
    override val hasChildren = true

    override fun readLines(scope: CoroutineScope): TextLines {
        return object : TextLines {
            override val size get() = 0
            override fun get(index: Int) = ""
        }
    }
}