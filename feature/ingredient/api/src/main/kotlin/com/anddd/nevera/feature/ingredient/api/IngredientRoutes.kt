package com.anddd.nevera.feature.ingredient.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── 진입점 ────────────────────────────────────────────────────────────────────

/** 식재료 등록 흐름의 진입점. */
@Serializable
data object IngredientGraphRoute : NavKey

/**
 * 영수증 촬영 화면. 홈과 냉장고가 목적지로 삼는다.
 *
 * @param openGallery true이면 카메라 권한 결정 이후 갤러리를 자동으로 연다.
 */
@Serializable
data class OcrCaptureRoute(val openGallery: Boolean = false) : NavKey

// ── 등록 흐름 내부 단계 (OcrCaptureRoute로 진입한다) ──────────────────────────
//
// 아래 목적지들은 촬영 단계를 거쳐야 의미가 있다. 밖에서 직접 이동하면
// 인자가 유효하지 않아 인식 실패로 이어진다.

/** OCR 인식 결과 화면. imageUri는 촬영·갤러리 선택 결과다. */
@Serializable
data class IngredientRoute(val imageUri: String) : NavKey

/** OCR 인식 실패 화면. */
@Serializable
data object OcrErrorRoute : NavKey

/** 등록 완료 화면. totalCost는 인식 결과에서 계산된다. */
@Serializable
data class RegisterSuccessRoute(val totalCost: Int) : NavKey

/** 영수증 사진 상세. imageUri는 인식 결과 화면에서 넘어온다. */
@Serializable
data class PhotoDetailRoute(val imageUri: String) : NavKey
