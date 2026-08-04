package com.anddd.nevera.feature.ingredient.ocrcapture.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute

/**
 * 캡처 화면 진입
 *
 * @param openGallery true이면 카메라 권한 결정 이후 PhotoPicker 자동 실행
 */
fun NavController.navigateToIngredientCapture(
    openGallery: Boolean = false,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrCaptureRoute(openGallery), navOptions(builder))
}
