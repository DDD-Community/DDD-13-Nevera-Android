package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

/**
 * 이미지 URI를 OCR API로 분석하여 식재료 목록을 추출하는 UseCase
 *
 * @param imageUri Content URI 문자열 (갤러리 또는 카메라에서 선택한 이미지)
 */
class ExtractIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(imageUri: String): NeveraResult<List<OcrIngredient>, OcrExtractError> =
        ingredientRepository.extractIngredients(imageUri)
}
