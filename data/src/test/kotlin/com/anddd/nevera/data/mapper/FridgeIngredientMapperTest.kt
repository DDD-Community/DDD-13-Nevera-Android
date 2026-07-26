package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

/** 냉장고 목록 응답의 정렬 기준 변환과 날짜 파싱 규칙을 고정한다. */
class FridgeIngredientMapperTest {

    private fun response(
        expirationDate: String = "2026-12-31T00:00:00+09:00",
        createdAt: String = "2026-07-22T09:30:00+09:00",
    ) = FridgeIngredientResponse(
        id = 3L,
        name = "삼겹살",
        category = "MEAT",
        location = "FREEZER",
        quantity = 2,
        expirationDate = expirationDate,
        cost = 18_000,
        createdAt = createdAt,
    )

    @Test
    fun `정렬 기준이 서버 요청 문자열로 변환된다`() {
        assertEquals("EXPIRY_DATE", IngredientSortOrder.ExpiryDate.toApiString())
        assertEquals("LATEST", IngredientSortOrder.Latest.toApiString())
    }

    @Test
    fun `응답의 카테고리와 보관위치가 도메인 모델로 변환된다`() {
        val ingredient = response().toDomain()

        assertEquals(FoodCategory.MeatEggs, ingredient.category)
        assertEquals(StorageLocation.Freezer, ingredient.storageLocation)
    }

    @Test
    fun `유통기한 문자열이 날짜로 파싱된다`() {
        assertEquals(LocalDate.of(2026, 12, 31), response().toDomain().expiryDate)
    }

    @Test
    fun `유통기한은 응답에 담긴 오프셋 기준으로 해석된다`() {
        // 같은 순간이라도 오프셋이 다르면 날짜가 하루 달라진다.
        // KST 자정은 UTC로는 전날 15시이므로, 오프셋을 무시하고 UTC로 해석하면 하루가 밀린다.
        val ingredient = response(expirationDate = "2026-12-31T00:00:00+09:00").toDomain()

        assertEquals(LocalDate.of(2026, 12, 31), ingredient.expiryDate)
    }

    @Test
    fun `생성 시각 문자열이 순간으로 파싱된다`() {
        val expected = OffsetDateTime.of(2026, 7, 22, 9, 30, 0, 0, ZoneOffset.ofHours(9)).toInstant()

        assertEquals(expected, response().toDomain().createdAt)
    }

    @Test
    fun `파싱할 수 없는 유통기한은 오늘 날짜로 대체된다`() {
        // 서버가 형식을 바꾸면 앱이 죽는 대신 오늘 날짜를 쓴다. 의도된 방어이지만
        // 화면에는 잘못된 유통기한이 보이므로 동작을 명시적으로 남겨 둔다.
        assertEquals(LocalDate.now(), response(expirationDate = "형식이-아닌-값").toDomain().expiryDate)
    }

    @Test
    fun `나머지 필드는 그대로 옮겨진다`() {
        val ingredient = response().toDomain()

        assertEquals(3L, ingredient.id)
        assertEquals("삼겹살", ingredient.name)
        assertEquals(2, ingredient.quantity)
        assertEquals(18_000, ingredient.cost)
    }
}
