package ui.filetree

import VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pointerMoveFilter

@Composable
fun FileTreeView(model: FileTree, selectedFile: io.File?, modifier: Modifier = Modifier) =
    with(LocalDensity.current) {
        Box(modifier = modifier) {
            val index = model.items.indexOfFirst { it.fullFile.path == selectedFile?.path }

            val scrollState = rememberLazyListState(if(index > -1) { kotlin.math.max(index - 5, 0) } else { 0 })
            val fontSize = 14.sp
            val lineHeight = fontSize.toDp() * 1.5f

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
            ) {
                items(model.items) {
                    FileTreeItemView(fontSize, lineHeight, it, it.fullFile.path == selectedFile?.path)
                }
            }

            VerticalScrollbar(
                Modifier.align(Alignment.CenterEnd),
                scrollState,
                model.items.size,
                lineHeight
            )
        }
    }

@Composable
private fun FileTreeItemView(fontSize: TextUnit, height: Dp, model: FileTree.Item, selected: Boolean) {
    Surface(color = if(selected) { Color.Gray } else { Color.Transparent }) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .clickable {
                    model.open()
                }
                .padding(start = 24.dp * model.level)
                .height(height)
                .fillMaxWidth(),
        ) {
            val active = remember { mutableStateOf(false) }

            FileItemIcon(Modifier.align(Alignment.CenterVertically), model)
            Text(
                text = model.fullFile.name,
                color = if (active.value) Color.LightGray else Color.White, //AmbientContentColor.current.copy(alpha = 0.60f) else AmbientContentColor.current,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clipToBounds()
                    .pointerMoveFilter(
                        onEnter = {
                            active.value = true
                            true
                        },
                        onExit = {
                            active.value = false
                            true
                        },
                        onMove = {
                            false
                        }
                    ),
                softWrap = true,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun FileItemIcon(modifier: Modifier, model: FileTree.Item) = Box(modifier.size(24.dp).padding(4.dp)) {
    when (val type = model.type) {
        is FileTree.ItemType.Folder -> when {
            !type.canExpand -> Unit
            type.isExpanded -> Icon(Icons.Default.KeyboardArrowDown, contentDescription = "file type icon", tint = Color.White)
            else -> Icon(Icons.Default.KeyboardArrowRight, contentDescription = "file type icon", tint = Color.White)
        }
        is FileTree.ItemType.File -> when (type.ext) {
            "kt" -> Icon(Icons.Default.Code, contentDescription = "file type icon", tint = Color(0xFF3E86A0))
            "xml" -> Icon(Icons.Default.Code, contentDescription = "file type icon", tint = Color(0xFFC19C5F))
            "txt" -> Icon(Icons.Default.Description, contentDescription = "file type icon", tint = Color(0xFF87939A))
            "md" -> Icon(Icons.Default.Description, contentDescription = "file type icon", tint = Color(0xFF87939A))
            "gitignore" -> Icon(Icons.Default.BrokenImage, contentDescription = "file type icon", tint = Color(0xFF87939A))
            "gradle" -> Icon(Icons.Default.Build, contentDescription = "file type icon", tint = Color(0xFF87939A))
            "kts" -> Icon(Icons.Default.Build, contentDescription = "file type icon", tint = Color(0xFF3E86A0))
            "properties" -> Icon(Icons.Default.Settings, contentDescription = "file type icon", tint = Color(0xFF62B543))
            "bat" -> Icon(Icons.Default.Launch, contentDescription = "file type icon", tint = Color(0xFF87939A))
            else -> Icon(Icons.Default.TextSnippet, contentDescription = "file type icon", tint = Color(0xFF87939A))
        }
    }
}