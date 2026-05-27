package com.anddd.nevera.feature.ingredient.ocrcapture.model

sealed interface OcrCaptureMode {
    data object Camera : OcrCaptureMode
    data object Gallery : OcrCaptureMode
}
