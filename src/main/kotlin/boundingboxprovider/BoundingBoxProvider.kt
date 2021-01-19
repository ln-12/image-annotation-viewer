package boundingboxprovider

import model.BoundingBox

interface BoundingBoxProvider {
    fun getBoundingBoxesForFile(path: String): List<BoundingBox>
}