package com.anddd.nevera.feature.ingredient.ocrcapture.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraState

data class OcrCaptureUiState(
    val mode: OcrCaptureMode = OcrCaptureMode.Camera,
    val galleryImages: List<Uri> = emptyList(),
) : NeveraState
