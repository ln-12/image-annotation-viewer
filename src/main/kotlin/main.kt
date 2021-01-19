import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import boundingboxprovider.PascalVocBoundingBoxProvider
import io.readImage
import io.toByteArray
import model.AnnotatedImage
import model.BoundingBox
import model.DataModel
import org.jetbrains.skija.Image
import ui.Clickable
import ui.common.AppTheme
import java.awt.image.BufferedImage


fun main() = Window(size = IntSize(1280, 768)) {
    MaterialTheme {
        setContent()
    }
}


@Composable
fun setContent() {
    val model = remember { DataModel() }
    Surface(color = AppTheme.colors.Gray) {
        Column {
            setTitleBar(text = "Image Annotation Viewer")
            Row {
                AnnotatedImageArea(model, modifier = Modifier.weight(1f))
                ImageSelectorArea(model, modifier = Modifier.preferredWidth(400.dp))
            }
        }
    }
}

@Composable
fun setTitleBar(text: String) {
    val refreshButtonHover = remember { mutableStateOf(false) }
    TopAppBar(
        backgroundColor = AppTheme.colors.DarkGray,
        title = {
            Row(Modifier.preferredHeight(50.dp)) {
                Text(
                    text,
                    color = AppTheme.colors.White,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                )

                Surface(
                    color = AppTheme.colors.Transparent,
                    modifier = Modifier.padding(end = 20.dp).align(Alignment.CenterVertically),
                    shape = CircleShape
                ) {
                    Clickable(
                        modifier = Modifier.hover(
                            onEnter = {
                                refreshButtonHover.value = true
                                false
                            },
                            onExit = {
                                refreshButtonHover.value = false
                                false
                            },
                            onMove = {
                                false
                            }
                        ).background(color = if (refreshButtonHover.value) AppTheme.colors.TranslucentBlack else AppTheme.colors.Transparent),
                        onClick = {
                            Window(
                                title = "About",
                                size = IntSize(500, 300),
                                location = IntOffset(100, 100),
                                centered = false
                            ) {
                                Surface(color = AppTheme.colors.DarkGray, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("Image Annotation Viewer", color = AppTheme.colors.White)
                                        Text("(C) 2021 LN-12", color = AppTheme.colors.White)
                                        Text("", color = AppTheme.colors.White)
                                        Text("UI: https://www.jetbrains.com/lp/compose/", color = AppTheme.colors.White)
                                        Text("Icons: https://material.io/resources/icons/?style=outline", color = AppTheme.colors.White)
                                    }
                                }
                            }
                        }
                    ) {
                        Image(
                            imageResource("icons/outline_info_white_18dp.png"),
                            modifier = Modifier.preferredSize(24.dp)
                        )
                    }
                }


            }
        })
}

@Composable
fun AnnotationPlaceable(
    modifier: Modifier = Modifier,
    imageSize: Size,
    boundingBox: BoundingBox,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // get the image scale
        val scale = ContentScale.Fit.computeScaleFactor(imageSize, Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()))

        // get the unscaled position
        val start = boundingBox.minX
        val top = boundingBox.minY

        // measure children
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // create layout
        layout(constraints.maxWidth, constraints.maxHeight) {
            // position children
            placeables.forEach { placeable ->
                val gapBetweenImageAndBorder = (constraints.maxWidth - (imageSize.width * scale.scaleX)) / 2.0
                val scaledX = start * scale.scaleX
                val scaledY = top * scale.scaleY

                // place the child at the correct location
                // add extra 5 to avoid overlapping
                placeable.placeRelative(x = (gapBetweenImageAndBorder + scaledX).toInt() + 5, y = scaledY.toInt())
            }
        }
    }
}

@Composable
fun AnnotatedImageArea(model: DataModel, modifier: Modifier = Modifier) {
    Card(
        backgroundColor = AppTheme.colors.LightGray,
        modifier = modifier.fillMaxSize(),
        shape = RectangleShape,
        elevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            val file = model.annotatedImage.value

            if(file == null) {
                Text(text = "No data selected")
            } else {
                Image(
                    Image.makeFromEncoded(file.image.toByteArray()).asImageBitmap(),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )

                file.boundingBoxes.forEach { boundingBox ->
                    val width = boundingBox.maxX - boundingBox.minX
                    val height = boundingBox.maxY - boundingBox.minY

                    val start = boundingBox.minX
                    val end = start + width
                    val top = boundingBox.minY
                    val bottom = top + height

                    val img = BufferedImage(file.image.width, file.image.height, BufferedImage.TYPE_INT_ARGB)
                    val color = java.awt.Color((0..255).random(), (0..255).random(), (0..255).random(), 255).rgb

                    for(x in start until end) {
                        img.setRGB(x, top, color)
                        img.setRGB(x, bottom, color)
                    }

                    for(y in top until bottom) {
                        img.setRGB(start, y, color)
                        img.setRGB(start, y, color)
                    }

                    println("$start $top")

                    Image(
                        Image.makeFromEncoded(img.toByteArray()).asImageBitmap(),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )

                    AnnotationPlaceable(boundingBox = boundingBox, imageSize = Size(img.width.toFloat(), img.height.toFloat())) {
                        Text(text = boundingBox.name, color = Color(color))
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePreview(
    model: DataModel,
    annotatedImage: AnnotatedImage
) {
    val cardHover = remember { mutableStateOf(false) }
    val infoButtonHover = remember { mutableStateOf(false) }
    Card(
        backgroundColor = if (cardHover.value) AppTheme.colors.MiniatureHoverColor else AppTheme.colors.MiniatureColor,
        modifier = Modifier.padding(end = 10.dp).preferredHeight(70.dp)
            .fillMaxWidth()
            .hover(
                onEnter = {
                    cardHover.value = true
                    false
                },
                onExit = {
                    cardHover.value = false
                    false
                },
                onMove = {
                    true
                }
            )
            .clickable {
                model.annotatedImage.value = annotatedImage
            },
        shape = RectangleShape
    ) {
        Row(modifier = Modifier.padding(end = 30.dp)) {
            Image(
                Image.makeFromEncoded(annotatedImage.image.toByteArray()).asImageBitmap(),
                modifier = Modifier.preferredHeight(70.dp)
                    .preferredWidth(90.dp)
                    .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 1.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = annotatedImage.path.fileName.toString(),
                color = AppTheme.colors.Foreground,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun ImageSelectorArea(model: DataModel, modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
         Box(modifier = Modifier.weight(1f)) {
            val stateVertical = rememberScrollState(0f)
            ScrollableColumn(scrollState = stateVertical) {
                var index = 1
                Column {
                    Spacer(modifier = Modifier.height(5.dp))

                    for (item in model.selectedImages.value) {
                        ImagePreview(
                            model = model,
                            annotatedImage = item
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        index++
                    }
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(stateVertical),
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
            )
        }

        Spacer(modifier = Modifier.preferredHeight(8.dp))

        Button(
            onClick = {
                ChooseFileDialog(onItemSelected = { file ->
                    val files = if(file.isDirectory) {
                        file.children.filter { it.name.endsWith(".png") }
                    } else {
                        listOf(file)
                    }.map {
                        // TODO support more formats
                        val boundingBox = PascalVocBoundingBoxProvider.getBoundingBoxesForFile(it.path.toString().replace(".png", ".xml"))
                        AnnotatedImage(it.path, readImage(it)!!, boundingBox)
                    }

                    model.selectedImages.value = files
                    model.annotatedImage.value = files[0]

                    println("Selected images: ${files.map { it.path.fileName.toString() }}")
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose directory")
        }
    }
}
