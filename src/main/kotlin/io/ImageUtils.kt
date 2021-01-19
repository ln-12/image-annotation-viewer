package io

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

fun readImage(file: File): BufferedImage? {
    var img: BufferedImage? = null
    try {
        img = ImageIO.read(file.path.toFile())
    } catch (e: IOException) {
        println("ERROR: " + e.message)
    }

    return img
}

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return baos.toByteArray()
}