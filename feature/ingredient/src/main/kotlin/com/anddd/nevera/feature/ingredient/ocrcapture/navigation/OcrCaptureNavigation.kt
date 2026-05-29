package com.anddd.nevera.feature.ingredient.ocrcapture.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import kotlinx.serialization.Serializable

@Serializable
internal data class OcrCaptureRoute(val mode: OcrCaptureMode = OcrCaptureMode.Camera)

/**
 * 홈 화면 FAB → 캡처 모드 선택 후 OcrCaptureScreen 진입
 *
 * @param mode 카메라 또는 갤러리 모드
 */
fun NavController.navigateToIngredientCapture(
    mode: OcrCaptureMode = OcrCaptureMode.Camera,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrCaptureRoute(mode), navOptions(builder))
}
