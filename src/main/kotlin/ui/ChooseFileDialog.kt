import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.Settings
import io.File
import io.createSystemFile
import io.toProjectFile
import ui.common.AppTheme
import ui.filetree.ExpandableFile
import ui.filetree.FileTree
import ui.filetree.FileTreeView

@OptIn(ExperimentalSettingsImplementation::class)
@Composable
fun ChooseFileDialog(onItemSelected: (File) -> Unit, onCloseRequest: () -> Unit) {
    Dialog(
        title = "Select file",
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(size = WindowSize(1024.dp, 768.dp))
    ) {
        val selectedFile = remember { mutableStateOf<File?>(null) }
        val fileTree = remember {
            // TODO in large directories, the scanning is pretty slow
            //      a progress indicator and handling in thew background with coroutines would be a good idea
            val settings = Settings()
            val path: String? = settings.getStringOrNull("path")

            val roots = java.io.File.listRoots()

            val projectFile = if(roots.size == 1) {
                roots[0].toProjectFile()
            } else {
                createSystemFile(roots)
            }

            val expandableRoot = ExpandableFile(projectFile, 0, false).apply {
                toggleExpanded()
            }

            if(path != null) {
                val directories = arrayListOf<String>()
                val fullPath = java.io.File(path)
                var parent: java.io.File? = fullPath

                while(parent != null) {
                    if(parent.name.isNotEmpty()) {
                        directories.add(parent.name)
                    }
                    parent = parent.parentFile
                }

                directories.reverse()

                var children: List<ExpandableFile>? = expandableRoot.children

                for(directory in directories) {
                    val child = children?.firstOrNull { it.file.name == directory }

                    child?.toggleExpanded()

                    children = child?.children
                }

                selectedFile.value = fullPath.toProjectFile()
            }

            FileTree(expandableRoot, onFileSelected = {
                selectedFile.value = it
            })
        }

        Surface(color = AppTheme.colors.DarkGray, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Column {
                Surface(color = AppTheme.colors.Gray, modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f)) {
                    FileTreeView(
                        fileTree,
                        selectedFile.value,
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().height(55.dp), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            val file = selectedFile.value

                            if(file != null) {
                                onItemSelected(file)

                                val settings = Settings()
                                val filePath = if(file.isDirectory) { file.path.toString() } else { file.path.parent.toString() }
                                settings.putString("path", filePath)

                                onCloseRequest()
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Select")
                    }

                    Button(
                        onClick = {
                            onCloseRequest()
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