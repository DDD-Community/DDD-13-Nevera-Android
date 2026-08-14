package com.anddd.nevera.feature.ingredient.main

import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import java.time.LocalDate

/**
 * [OcrIngredient] → [IngredientUiModel] 변환
 * OCR API 결과를 UI 모델로 변환할 때 사용합니다.
 */
internal fun OcrIngredient.toUiModel(): IngredientUiModel = IngredientUiModel(
    name = name,
    category = category,
    location = location,
    quantity = quantity,
    expiryDate = expiryDate ?: LocalDate.now().plusDays(7),
    cost = cost,
)

/** [List<OcrIngredient>] → [List<IngredientUiModel>] 일괄 변환 */
internal fun List<OcrIngredient>.toUiModels(): List<IngredientUiModel> = map { it.toUiModel() }

/** [List<IngredientUiModel>] → [List<OcrIngredient>] 일괄 변환 */
internal fun List<IngredientUiModel>.toDomains(): List<OcrIngredient> = map { it.toDomain() }

/**
 * [IngredientUiModel] → [OcrIngredient] 변환
 * 등록 API 호출 시 선택된 UI 모델을 도메인 모델로 변환합니다.
 * - category null → [OcrIngredient.DEFAULT_CATEGORY] 기본값
 * - location null → [OcrIngredient.DEFAULT_LOCATION] 기본값
 */
internal fun IngredientUiModel.toDomain(): OcrIngredient = OcrIngredient(
    name = name,
    category = category ?: OcrIngredient.DEFAULT_CATEGORY,
    location = location ?: OcrIngredient.DEFAULT_LOCATION,
    quantity = quantity,
    expiryDate = expiryDate,
    cost = cost,
)
