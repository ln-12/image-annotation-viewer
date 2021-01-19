import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerMoveFilter

fun Modifier.pointerMoveFilter(
    onEnter: () -> Boolean,
    onExit: () -> Boolean,
    onMove: (Offset) -> Boolean
): Modifier = this.pointerMoveFilter(onEnter = onEnter, onExit = onExit, onMove = onMove)

fun Modifier.hover(
    onEnter: () -> Boolean,
    onExit: () -> Boolean,
    onMove: (Offset) -> Boolean
): Modifier = this.pointerMoveFilter(onEnter = onEnter, onExit = onExit, onMove = onMove)
