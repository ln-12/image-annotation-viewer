package model

import java.awt.image.BufferedImage
import java.nio.file.Path

data class AnnotatedImage(val path: Path, val image: BufferedImage, val boundingBoxes: List<BoundingBox>)
