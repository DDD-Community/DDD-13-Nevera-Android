package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

/**
 * 선택된 식재료를 서버에 등록하는 UseCase
 *
 * ⚠️ 등록 API 명세 미제공 — 실제 에러 타입은 API 확정 후 도메인 에러로 교체하세요.
 *
 * @param items 등록할 식재료 목록 (isSelected = true인 항목만 전달)
 */
class RegisterIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(items: List<OcrIngredient>): NeveraResult<Unit, NetworkError> =
        ingredientRepository.registerIngredients(items)
}
