package com.anddd.nevera.feature.ingredient.main

import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel

/**
 * [OcrIngredient] → [IngredientUiModel] 변환
 * OCR API 결과를 UI 모델로 변환할 때 사용합니다.
 */
internal fun OcrIngredient.toUiModel(): IngredientUiModel = IngredientUiModel(
    name = name,
    category = category,
    location = location,
    quantity = quantity,
    expiryDate = expiryDate,
    cost = cost,
)

/** [List<OcrIngredient>] → [List<IngredientUiModel>] 일괄 변환 */
internal fun List<OcrIngredient>.toUiModels(): List<IngredientUiModel> = map { it.toUiModel() }

/** [List<IngredientUiModel>] → [List<OcrIngredient>] 일괄 변환 */
internal fun List<IngredientUiModel>.toDomains(): List<OcrIngredient> = map { it.toDomain() }

/**
 * [IngredientUiModel] → [OcrIngredient] 변환
 * 등록 API 호출 시 선택된 UI 모델을 도메인 모델로 변환합니다.
 * - category null → [FoodCategory.Other] 기본값
 * - location null → [StorageLocation.Pantry] 기본값
 */
internal fun IngredientUiModel.toDomain(): OcrIngredient = OcrIngredient(
    name = name,
    category = category ?: FoodCategory.Other,
    location = location ?: StorageLocation.Pantry,
    quantity = quantity,
    expiryDate = expiryDate,
    cost = cost,
)
