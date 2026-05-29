package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError

interface IngredientRepository {

    /**
     * 이미지를 OCR API로 분석하여 식재료 목록 반환
     *
     * @param imageUri 분석할 이미지 경로
     */
    suspend fun extractIngredients(imageUri: String): NeveraResult<List<OcrIngredient>, OcrExtractError>

    suspend fun registerIngredients(items: List<OcrIngredient>): NeveraResult<Unit, RegisterIngredientError>

    suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>

    suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>
}
