package com.anddd.nevera.domain.model.ingredient

import java.time.LocalDate

/**
 * OCR 분석 결과로 추출된 식재료 도메인 모델
 *
 * @param name      식재료명
 * @param category  카테고리 ([FoodCategory])
 * @param location  보관 방법 ([StorageLocation])
 * @param quantity  수량 (최소 1)
 * @param expiryDate 유통기한 (null = 미제공)
 * @param cost      금액 (원)
 */
data class OcrIngredient(
    val name: String,
    val category: FoodCategory,
    val location: StorageLocation,
    val quantity: Int,
    val expiryDate: LocalDate?,
    val cost: Int,
) {
    companion object {
        val DEFAULT_CATEGORY = FoodCategory.Other
        val DEFAULT_LOCATION = StorageLocation.Pantry
    }
}
