package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * 서버 문자열과 도메인 모델 사이의 변환 규칙을 고정한다.
 *
 * 카테고리는 서버가 같은 개념에 두 가지 문자열을 쓰고("VEG"와 "VEGETABLE"),
 * 매핑되지 않는 값은 조용히 Etc로 떨어진다. 알 수 없는 값이 앱을 멈추지 않는 것은
 * 의도된 설계지만, 그 때문에 매핑이 하나 빠져도 아무 증상이 없다는 뜻이기도 하다.
 */
class IngredientMapperTest {

    @Test
    fun `서버 카테고리 문자열이 대응하는 FoodCategory로 변환된다`() {
        assertEquals(FoodCategory.Veg, "VEG".toFoodCategory())
        assertEquals(FoodCategory.Fruit, "FRUIT".toFoodCategory())
        assertEquals(FoodCategory.MeatEggs, "MEATEGGS".toFoodCategory())
        assertEquals(FoodCategory.Sea, "SEA".toFoodCategory())
        assertEquals(FoodCategory.Dairy, "DAIRY".toFoodCategory())
        assertEquals(FoodCategory.Sauce, "SAUCE".toFoodCategory())
        assertEquals(FoodCategory.Drink, "DRINK".toFoodCategory())
        assertEquals(FoodCategory.Processed, "CANDRY".toFoodCategory())
    }

    @Test
    fun `같은 카테고리를 가리키는 서버 별칭 문자열도 동일하게 변환된다`() {
        assertEquals(FoodCategory.Veg, "VEGETABLE".toFoodCategory())
        assertEquals(FoodCategory.MeatEggs, "MEAT".toFoodCategory())
        assertEquals(FoodCategory.MeatEggs, "EGG".toFoodCategory())
        assertEquals(FoodCategory.Sea, "SEAFOOD".toFoodCategory())
        assertEquals(FoodCategory.Sauce, "SEASONING".toFoodCategory())
        assertEquals(FoodCategory.Drink, "BEVERAGE".toFoodCategory())
        assertEquals(FoodCategory.Processed, "PROCESSED".toFoodCategory())
    }

    @Test
    fun `곡물류는 전용 카테고리가 없어 Etc로 변환된다`() {
        // FoodCategory에 곡물 항목이 생기면 이 테스트가 실패해야 한다.
        assertEquals(FoodCategory.Etc, "GRAINS".toFoodCategory())
    }

    @Test
    fun `알 수 없는 카테고리 문자열은 Etc로 변환된다`() {
        assertEquals(FoodCategory.Etc, "알수없는값".toFoodCategory())
        assertEquals(FoodCategory.Etc, "".toFoodCategory())
        assertEquals(FoodCategory.Etc, "veg".toFoodCategory())
    }

    @Test
    fun `서버 보관위치 문자열이 대응하는 StorageLocation으로 변환된다`() {
        assertEquals(StorageLocation.Fridge, "FRIDGE".toStorageLocation())
        assertEquals(StorageLocation.Freezer, "FREEZER".toStorageLocation())
    }

    @Test
    fun `알 수 없는 보관위치 문자열은 Pantry로 변환된다`() {
        assertEquals(StorageLocation.Pantry, "PANTRY".toStorageLocation())
        assertEquals(StorageLocation.Pantry, "알수없는값".toStorageLocation())
        assertEquals(StorageLocation.Pantry, "".toStorageLocation())
    }

    @Test
    fun `FoodCategory가 서버 요청 문자열로 되돌아간다`() {
        assertEquals("VEG", FoodCategory.Veg.toApiString())
        assertEquals("FRUIT", FoodCategory.Fruit.toApiString())
        assertEquals("MEATEGGS", FoodCategory.MeatEggs.toApiString())
        assertEquals("SEA", FoodCategory.Sea.toApiString())
        assertEquals("DAIRY", FoodCategory.Dairy.toApiString())
        assertEquals("SAUCE", FoodCategory.Sauce.toApiString())
        assertEquals("DRINK", FoodCategory.Drink.toApiString())
        assertEquals("PROCESSED", FoodCategory.Processed.toApiString())
        assertEquals("ETC", FoodCategory.Etc.toApiString())
    }

    @Test
    fun `모든 FoodCategory는 요청 문자열로 바꿨다 되돌려도 같은 값이 된다`() {
        // PROCESSED는 응답에서 CANDRY로도 오지만, 요청에는 PROCESSED만 쓰므로 왕복이 성립한다.
        FoodCategory.entries.forEach { category ->
            assertEquals(category, category.toApiString().toFoodCategory())
        }
    }

    @Test
    fun `StorageLocation이 서버 요청 문자열로 되돌아간다`() {
        assertEquals("FRIDGE", StorageLocation.Fridge.toApiString())
        assertEquals("FREEZER", StorageLocation.Freezer.toApiString())
        assertEquals("PANTRY", StorageLocation.Pantry.toApiString())
    }

    @Test
    fun `모든 StorageLocation은 요청 문자열로 바꿨다 되돌려도 같은 값이 된다`() {
        StorageLocation.entries.forEach { location ->
            assertEquals(location, location.toApiString().toStorageLocation())
        }
    }

    @Test
    fun `유통기한은 한국 시간 자정 기준 ISO 문자열로 변환된다`() {
        val date = LocalDate.of(2026, 7, 22)

        // ISO_OFFSET_DATE_TIME은 LocalTime.toString()과 달리 0초를 생략하지 않는다.
        assertEquals("2026-07-22T00:00:00+09:00", date.toApiExpirationDate())
    }

    @Test
    fun `유통기한 변환은 실행 환경의 기본 시간대와 무관하게 항상 KST 오프셋을 붙인다`() {
        val defaultZone = java.util.TimeZone.getDefault()
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

            assertEquals("2026-01-01T00:00:00+09:00", LocalDate.of(2026, 1, 1).toApiExpirationDate())
        } finally {
            java.util.TimeZone.setDefault(defaultZone)
        }
    }

    @Test
    fun `IngredientResponse는 카테고리를 변환하고 나머지 필드를 그대로 옮긴다`() {
        val response = IngredientResponse(
            id = 7L,
            name = "당근",
            category = "VEGETABLE",
            categoryDisplayName = "채소",
            quantity = 3,
            cost = 4_500,
        )

        val ingredient = response.toDomain()

        assertEquals(7L, ingredient.id)
        assertEquals("당근", ingredient.name)
        assertEquals(FoodCategory.Veg, ingredient.category)
        assertEquals("채소", ingredient.categoryName)
        assertEquals(3, ingredient.quantity)
        assertEquals(4_500, ingredient.cost)
    }

    @Test
    fun `OCR 응답에는 유통기한이 없으므로 null로 변환된다`() {
        val dto = OcrIngredientDto(
            name = "우유",
            category = "DAIRY",
            location = "FRIDGE",
            quantity = 1,
            unit = "EA",
            cost = 3_000,
        )

        val ocrIngredient = dto.toDomain()

        assertNull(ocrIngredient.expiryDate)
        assertEquals(FoodCategory.Dairy, ocrIngredient.category)
        assertEquals(StorageLocation.Fridge, ocrIngredient.location)
    }

    @Test
    fun `유통기한이 없는 OCR 식재료를 등록 요청으로 바꾸면 유통기한이 null이 된다`() {
        val ocrIngredient = OcrIngredient(
            name = "우유",
            category = FoodCategory.Dairy,
            location = StorageLocation.Fridge,
            quantity = 1,
            expiryDate = null,
            cost = 3_000,
        )

        val request = ocrIngredient.toRequest()

        assertNull(request.expirationDate)
        assertEquals("DAIRY", request.category)
        assertEquals("FRIDGE", request.location)
    }

    @Test
    fun `유통기한이 있는 OCR 식재료를 등록 요청으로 바꾸면 KST 자정 문자열이 된다`() {
        val ocrIngredient = OcrIngredient(
            name = "우유",
            category = FoodCategory.Dairy,
            location = StorageLocation.Fridge,
            quantity = 1,
            expiryDate = LocalDate.of(2026, 12, 31),
            cost = 3_000,
        )

        assertEquals("2026-12-31T00:00:00+09:00", ocrIngredient.toRequest().expirationDate)
    }
}
