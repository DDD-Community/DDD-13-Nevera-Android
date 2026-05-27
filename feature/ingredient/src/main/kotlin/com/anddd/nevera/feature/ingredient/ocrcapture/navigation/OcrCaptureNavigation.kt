package com.anddd.nevera.feature.ingredient.ocrcapture.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

internal const val OCR_CAPTURE_INITIAL_MODE_ARG = "initialMode"
internal const val GALLERY_MODE_VALUE = "gallery"

@Serializable
internal data class OcrCaptureRoute(val initialMode: String = "")

fun NavController.navigateToOcrCapture(
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrCaptureRoute(), navOptions(builder))
}

