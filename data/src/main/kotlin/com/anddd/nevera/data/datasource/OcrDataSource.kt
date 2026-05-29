package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto

internal interface OcrDataSource {

    /**
     * 이미지 URI 문자열을 받아 OCR API를 호출하고 결과 DTO 목록 반환
     *
     * @param imageUri Content URI 문자열
     */
    suspend fun extractIngredients(imageUri: String): ApiResponse<List<OcrIngredientDto>>
}
