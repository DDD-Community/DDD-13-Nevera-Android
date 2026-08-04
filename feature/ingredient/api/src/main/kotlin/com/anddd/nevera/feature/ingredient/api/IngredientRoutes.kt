package com.anddd.nevera.feature.ingredient.api

import kotlinx.serialization.Serializable

/** 식재료 등록 흐름의 진입점. */
@Serializable
data object IngredientGraphRoute

/**
 * 영수증 촬영 화면. 홈과 냉장고가 목적지로 삼는다.
 *
 * @param openGallery true이면 카메라 권한 결정 이후 갤러리를 자동으로 연다.
 */
@Serializable
data class OcrCaptureRoute(val openGallery: Boolean = false)
