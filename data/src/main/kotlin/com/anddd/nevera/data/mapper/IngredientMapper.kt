package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.StorageLocation

/**
 * API 응답 category 값 → FoodCategory 변환
 * 매핑 실패 시 [FoodCategory.Other] 반환
 */
internal fun String.toFoodCategory(): FoodCategory = when (this) {
    "VEGETABLE"           -> FoodCategory.Vegetable
    "FRUIT"               -> FoodCategory.Fruit
    "MEAT", "EGG"         -> FoodCategory.MeatEgg
    "SEAFOOD"             -> FoodCategory.Seafood
    "DAIRY"               -> FoodCategory.Dairy
    "SAUCE", "SEASONING"  -> FoodCategory.Sauce
    "BEVERAGE"            -> FoodCategory.Beverage
    "CANDRY", "PROCESSED" -> FoodCategory.Processed
    "GRAINS"              -> FoodCategory.Other  // 곡물류 — FoodCategory에 전용 항목 없어 Other로 임시 매핑
    else                  -> FoodCategory.Other
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

/**
 * [OcrIngredientDto] → [OcrIngredient] 도메인 모델 변환
 *
 * ※ OCR API 응답에 expiryDate가 없으므로 null로 설정됩니다.
 *    유통기한은 사용자가 UI에서 직접 입력합니다.
 */
internal fun OcrIngredientDto.toDomain(): OcrIngredient = OcrIngredient(
    name = name,
    category = category.toFoodCategory(),
    location = location.toStorageLocation(),
    quantity = quantity,
    expiryDate = null,
    cost = cost,
)

/**
 * [FoodCategory] → API 요청 문자열 변환
 */
internal fun FoodCategory.toApiString(): String = when (this) {
    FoodCategory.Vegetable  -> "VEGETABLE"
    FoodCategory.Fruit      -> "FRUIT"
    FoodCategory.MeatEgg    -> "MEAT"
    FoodCategory.Seafood    -> "SEAFOOD"
    FoodCategory.Dairy      -> "DAIRY"
    FoodCategory.Sauce      -> "SAUCE"
    FoodCategory.Beverage   -> "BEVERAGE"
    FoodCategory.Processed  -> "PROCESSED"
    FoodCategory.Other      -> "ETC"
}

/**
 * [StorageLocation] → API 요청 문자열 변환
 */
internal fun StorageLocation.toApiString(): String = when (this) {
    StorageLocation.Fridge  -> "FRIDGE"
    StorageLocation.Freezer -> "FREEZER"
    StorageLocation.Pantry  -> "PANTRY"
}
