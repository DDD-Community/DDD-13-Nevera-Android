package com.anddd.nevera.domain.model.ingredient

import java.time.LocalDate

/**
 * 식재료 수정 요청에 필요한 입력값
 *
 * @param name      식재료명
 * @param category  카테고리 ([FoodCategory])
 * @param location  보관 방법 ([StorageLocation])
 * @param quantity  수량
 * @param expiryDate 유통기한
 * @param cost      금액 (원)
 */
data class EditIngredientInput(
    val name: String,
    val category: FoodCategory,
    val location: StorageLocation,
    val quantity: Int,
    val expiryDate: LocalDate,
    val cost: Int,
)
