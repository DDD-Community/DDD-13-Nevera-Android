package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.ingredient.EditIngredientRequest
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest
import com.anddd.nevera.domain.model.ingredient.EditIngredientInput
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val KST_ZONE_ID = ZoneId.of("Asia/Seoul")

/**
 * [LocalDate] → API 요청용 ISO-8601 offset 문자열 변환 (KST 기준 00:00)
 */
internal fun LocalDate.toApiExpirationDate(): String =
    atStartOfDay(KST_ZONE_ID).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

/**
 * API 응답 category 값 → FoodCategory 변환
 * 매핑 실패 시 [FoodCategory.Etc] 반환
 */
internal fun String.toFoodCategory(): FoodCategory = when (this) {
    "VEG", "VEGETABLE" -> FoodCategory.Veg
    "FRUIT" -> FoodCategory.Fruit
    "MEATEGGS", "MEAT", "EGG" -> FoodCategory.MeatEggs
    "SEA", "SEAFOOD" -> FoodCategory.Sea
    "DAIRY" -> FoodCategory.Dairy
    "SAUCE", "SEASONING" -> FoodCategory.Sauce
    "DRINK", "BEVERAGE" -> FoodCategory.Drink
    "CANDRY", "PROCESSED" -> FoodCategory.Processed
    "GRAINS"              -> FoodCategory.Etc  // 곡물류 — FoodCategory에 전용 항목 없어 Etc로 임시 매핑
    else                  -> FoodCategory.Etc
}

internal fun IngredientResponse.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name,
    category = category.toFoodCategory(),
    categoryName = categoryDisplayName,
    quantity = quantity,
    cost = cost,
)

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
    FoodCategory.Veg        -> "VEG"
    FoodCategory.Fruit      -> "FRUIT"
    FoodCategory.MeatEggs   -> "MEATEGGS"
    FoodCategory.Sea        -> "SEA"
    FoodCategory.Dairy      -> "DAIRY"
    FoodCategory.Sauce      -> "SAUCE"
    FoodCategory.Drink      -> "DRINK"
    FoodCategory.Processed  -> "PROCESSED"
    FoodCategory.Etc        -> "ETC"
}

/**
 * [StorageLocation] → API 요청 문자열 변환
 */
internal fun StorageLocation.toApiString(): String = when (this) {
    StorageLocation.Fridge  -> "FRIDGE"
    StorageLocation.Freezer -> "FREEZER"
    StorageLocation.Pantry  -> "PANTRY"
}

internal fun OcrIngredient.toRequest(): RegisterIngredientRequest = RegisterIngredientRequest(
    name = name,
    category = category.toApiString(),
    location = location.toApiString(),
    quantity = quantity,
    expirationDate = expiryDate?.toApiExpirationDate(),
    cost = cost,
)

internal fun EditIngredientInput.toRequest(): EditIngredientRequest = EditIngredientRequest(
    name = name,
    category = category.toApiString(),
    location = location.toApiString(),
    quantity = quantity,
    expirationDate = expiryDate.toApiExpirationDate(),
    cost = cost,
)
