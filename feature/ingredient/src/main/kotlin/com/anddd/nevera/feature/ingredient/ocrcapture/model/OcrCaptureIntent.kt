package com.anddd.nevera.feature.ingredient.ocrcapture.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface OcrCaptureIntent : NeveraIntent {
    data object Close : OcrCaptureIntent
    data object OpenGallery : OcrCaptureIntent
    data object EnsureGalleryIfNeeded : OcrCaptureIntent
    data object TakePicture : OcrCaptureIntent
    data object SwapCamera : OcrCaptureIntent
    data class SelectImage(val uri: Uri) : OcrCaptureIntent
    data object OpenCameraSettings : OcrCaptureIntent
    data class CameraPermissionUpdated(
        val hasPermission: Boolean,
        val isDenied: Boolean,
    ) : OcrCaptureIntent
}
