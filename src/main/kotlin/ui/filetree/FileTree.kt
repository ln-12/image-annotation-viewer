package ui.filetree

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.File

class ExpandableFile(
    val file: File,
    val level: Int,
    val hideFiles: Boolean
) {
    var children: List<ExpandableFile> by mutableStateOf(emptyList())
    val canExpand: Boolean get() = file.hasChildren

    fun toggleExpanded() {
        children = if (children.isEmpty()) {
            file.children
                .filter { if(hideFiles) { it.isDirectory } else { true } }
                .map { ExpandableFile(it, level + 1, hideFiles) }
                .sortedWith(compareBy({ it.file.isDirectory }, { it.file.name.toLowerCase() }))
                .sortedBy { !it.file.isDirectory }
        } else {
            emptyList()
        }
    }
}

class FileTree(root: File, hideFiles: Boolean, val onFileSelected: (File) -> Unit) {
    private val expandableRoot = ExpandableFile(root, 0, hideFiles).apply {
        toggleExpanded()
    }

    val items: List<Item> get() {
        return expandableRoot.toItems()
    }

    inner class Item constructor(
        private val file: ExpandableFile
    ) {
        val name: String get() = file.file.name

        val level: Int get() = file.level

        val type: ItemType
            get() = if (file.file.isDirectory) {
                ItemType.Folder(isExpanded = file.children.isNotEmpty(), canExpand = file.canExpand)
            } else {
                ItemType.File(ext = file.file.name.substringAfterLast(".").toLowerCase())
            }

        fun open() = when (type) {
            is ItemType.Folder -> {
                onFileSelected(file.file)
                file.toggleExpanded()
            }
            is ItemType.File -> onFileSelected(file.file)
        }
    }

    sealed class ItemType {
        class Folder(val isExpanded: Boolean, val canExpand: Boolean) : ItemType()
        class File(val ext: String) : ItemType()
    }

    private fun ExpandableFile.toItems(): List<Item> {
        fun ExpandableFile.addTo(list: MutableList<Item>) {
            list.add(Item(this))
            for (child in children) {
                child.addTo(list)
            }
        }

        val list = mutableListOf<Item>()
        addTo(list)
        return list
    }
}