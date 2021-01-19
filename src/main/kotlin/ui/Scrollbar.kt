import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: LazyListState,
    itemCount: Int,
    averageItemSize: Dp
) = androidx.compose.foundation.VerticalScrollbar(
    rememberScrollbarAdapter(scrollState, itemCount, averageItemSize),
    modifier
)