package com.anddd.nevera.feature.ingredient.ocrerror.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import kotlinx.serialization.Serializable

@Serializable
internal data class OcrErrorRoute(val captureMode: OcrCaptureMode = OcrCaptureMode.Camera)

internal fun NavController.navigateToOcrError(
    captureMode: OcrCaptureMode = OcrCaptureMode.Camera,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrErrorRoute(captureMode), navOptions(builder))
}
