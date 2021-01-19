import androidx.compose.desktop.AppManager
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.File
import io.createSystemFile
import io.toProjectFile
import ui.filetree.FileTree
import ui.filetree.FileTreeView


fun ChooseFileDialog(onItemSelected: (File) -> Unit) {
    Window(
        title = "Select file",
        size = IntSize(1024, 768),
        location = IntOffset(100, 100),
        centered = false
    ) {
        val selectedFile = remember { mutableStateOf<File?>(null) }
        val fileTree = remember {
            val roots = java.io.File.listRoots()

            val projectFile = if(roots.size == 1) {
                roots[0].toProjectFile()
            } else {
                createSystemFile(roots)
            }

            FileTree(projectFile, false, onFileSelected = {
                selectedFile.value = it
            })
        }

        Surface(color = Color.DarkGray, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Column {
                FileTreeView(
                    fileTree,
                    modifier = Modifier.weight(1f)
                )

                Row(modifier = Modifier.fillMaxWidth().preferredHeight(55.dp), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            val file = selectedFile.value

                            if(file != null) {
                                onItemSelected(file)
                                AppManager.focusedWindow?.close()
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Select")
                    }

                    Button(
                        onClick = {
                            AppManager.focusedWindow?.close()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}