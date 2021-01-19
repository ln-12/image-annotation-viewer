package boundingboxprovider

import model.BoundingBox
import java.util.*

// adopted from https://github.com/deeplearning4j/DataVec/blob/master/datavec-data/datavec-data-image/src/main/java/org/datavec/image/recordreader/objdetect/impl/VocLabelProvider.java
/*
 *  * Copyright 2017 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

object PascalVocBoundingBoxProvider: BoundingBoxProvider {
    private const val OBJECT_START_TAG = "<object>"
    private const val OBJECT_END_TAG = "</object>"
    private const val NAME_TAG = "<name>"
    private const val XMIN_TAG = "<xmin>"
    private const val YMIN_TAG = "<ymin>"
    private const val XMAX_TAG = "<xmax>"
    private const val YMAX_TAG = "<ymax>"

    override fun getBoundingBoxesForFile(path: String): List<BoundingBox> {
        try {
            val file = java.io.File(path)

            val lines = file.readLines()

            val out: MutableList<BoundingBox> = ArrayList()
            var i = 0
            while (i < lines.size) {
                if (!lines[i].contains(OBJECT_START_TAG)) {
                    i++
                    continue
                }
                var name: String? = null
                var xmin = Int.MIN_VALUE
                var ymin = Int.MIN_VALUE
                var xmax = Int.MIN_VALUE
                var ymax = Int.MIN_VALUE
                while (!lines[i].contains(OBJECT_END_TAG)) {
                    if (name == null && lines[i].contains(NAME_TAG)) {
                        val idxStartName = lines[i].indexOf('>') + 1
                        val idxEndName = lines[i].lastIndexOf('<')
                        name = lines[i].substring(idxStartName, idxEndName)
                        i++
                        continue
                    }
                    if (xmin == Int.MIN_VALUE && lines[i].contains(XMIN_TAG)) {
                        xmin = extractAndParse(lines[i])
                        i++
                        continue
                    }
                    if (ymin == Int.MIN_VALUE && lines[i].contains(YMIN_TAG)) {
                        ymin = extractAndParse(lines[i])
                        i++
                        continue
                    }
                    if (xmax == Int.MIN_VALUE && lines[i].contains(XMAX_TAG)) {
                        xmax = extractAndParse(lines[i])
                        i++
                        continue
                    }
                    if (ymax == Int.MIN_VALUE && lines[i].contains(YMAX_TAG)) {
                        ymax = extractAndParse(lines[i])
                        i++
                        continue
                    }
                    i++
                }
                checkNotNull(name) { "Invalid object format: no name tag found for object in file $path" }
                check(!(xmin == Int.MIN_VALUE || ymin == Int.MIN_VALUE || xmax == Int.MIN_VALUE || ymax == Int.MIN_VALUE)) { "Invalid object format: did not find all of xmin/ymin/xmax/ymax tags in $path" }
                out.add(BoundingBox(name, xmin, ymin, xmax, ymax))
                i++
            }

            return out
        } catch (ex: Exception) {
            println(ex.message)
            return emptyList()
        }
    }

    private fun extractAndParse(line: String): Int {
        val idxStartName = line.indexOf('>') + 1
        val idxEndName = line.lastIndexOf('<')
        val substring = line.substring(idxStartName, idxEndName)
        return substring.toInt()
    }
}