package com.anddd.nevera.feature.ingredient.ocrcapture.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface OcrCaptureSideEffect : NeveraSideEffect {
    data object NavigateBack : OcrCaptureSideEffect
    data class NavigateToResult(val uri: Uri) : OcrCaptureSideEffect
    data object OpenCameraSettings : OcrCaptureSideEffect
    data object OpenGallerySettings : OcrCaptureSideEffect
    data object ShowCaptureError : OcrCaptureSideEffect
}
