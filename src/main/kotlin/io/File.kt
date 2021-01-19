package io

import kotlinx.coroutines.CoroutineScope
import ui.util.TextLines
import java.nio.file.Path

interface File {
    val name: String
    val path: Path
    val isDirectory: Boolean
    val children: List<File>
    val hasChildren: Boolean

    fun readLines(scope: CoroutineScope): TextLines
}