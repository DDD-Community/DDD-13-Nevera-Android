package com.anddd.nevera.data.mapper

import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation

/**
 * API 응답 category 값 → FoodCategory 변환
 * 매핑 실패 시 [FoodCategory.Other] 반환
 */
internal fun String.toFoodCategory(): FoodCategory = when (this) {
    "VEGETABLE" -> FoodCategory.Vegetable
    "FRUIT" -> FoodCategory.Fruit
    "MEAT", "EGG" -> FoodCategory.MeatEgg
    "SEAFOOD" -> FoodCategory.Seafood
    "DAIRY" -> FoodCategory.Dairy
    "SAUCE", "SEASONING" -> FoodCategory.Sauce
    "BEVERAGE" -> FoodCategory.Beverage
    "CANDRY", "PROCESSED" -> FoodCategory.Processed
    else -> FoodCategory.Other
}

/**
 * API 응답 location 값 → StorageLocation 변환
 * 매핑 실패 시 [StorageLocation.Pantry] 반환
 */
internal fun String.toStorageLocation(): StorageLocation = when (this) {
    "FRIDGE" -> StorageLocation.Fridge
    "FREEZER" -> StorageLocation.Freezer
    else -> StorageLocation.Pantry
}
