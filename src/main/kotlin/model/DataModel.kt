package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class DataModel (
    var annotatedImage: MutableState<AnnotatedImage?> = mutableStateOf(null),
    var selectedImages: MutableState<List<AnnotatedImage>> = mutableStateOf(listOf()),
)
