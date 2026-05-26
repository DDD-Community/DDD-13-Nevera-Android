package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrIngredient

interface IngredientRepository {

    /**
     * 이미지 URI를 OCR API로 분석하여 식재료 목록 반환
     *
     * @param imageUri Content URI 문자열 (갤러리 또는 카메라에서 선택한 이미지)
     */
    suspend fun extractIngredients(imageUri: String): NeveraResult<List<OcrIngredient>, OcrExtractError>

    /**
     * 식재료 서버 등록
     *
     * ⚠️ 등록 API 명세 미제공 — 실제 API 준비 후 구현하세요.
     *    현재는 [NeveraResult.Success] 임시 반환합니다.
     *
     * @param items isSelected = true인 항목만 전달됩니다.
     */
    suspend fun registerIngredients(items: List<OcrIngredient>): NeveraResult<Unit, NetworkError>
}
