package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class FakeFridgeRemoteDataSource @Inject constructor() : FridgeRemoteDataSource {

    override suspend fun getFridgeIngredients(
        storageLocation: String?,
        category: String?,
        sortType: String,
        page: Int,
        size: Int,
    ): ApiResponse<FridgeIngredientsResponse> {
        val filtered = mockIngredients
            .filter { storageLocation == null || it.location == storageLocation }
            .filter { category == null || it.category == category }
        return ApiResponse(
            result = FridgeIngredientsResponse(
                content = filtered,
                last = true,
                number = 0,
            ),
            error = null,
        )
    }

    override suspend fun getFridgeIngredientById(id: Long): ApiResponse<FridgeIngredientResponse> {
        val ingredient = mockIngredients.first { it.id == id }
        return ApiResponse(result = ingredient, error = null)
    }

    private companion object {
        private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        private fun expiryDateOf(daysFromNow: Long): String {
            val date = LocalDate.now().plusDays(daysFromNow)
            return OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.ofHours(9)).format(formatter)
        }

        private fun createdAt(): String =
            OffsetDateTime.of(LocalDate.now().minusDays(7).atStartOfDay(), ZoneOffset.ofHours(9))
                .format(formatter)

        val mockIngredients = listOf(
            FridgeIngredientResponse(
                id = 1L, name = "제주 햇당근", category = "VEGETABLE", location = "FRIDGE",
                quantity = 2, expirationDate = expiryDateOf(28), dDay = 28, cost = 6500, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 2L, name = "한돈 삼겹살", category = "MEATEGGS", location = "FRIDGE",
                quantity = 1, expirationDate = expiryDateOf(-3), dDay = -3, cost = 24990, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 3L, name = "서울우유 1L", category = "DAIRY", location = "FRIDGE",
                quantity = 1, expirationDate = expiryDateOf(5), dDay = 5, cost = 3570, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 4L, name = "동물복지 유정란", category = "MEATEGGS", location = "FRIDGE",
                quantity = 10, expirationDate = expiryDateOf(4), dDay = 4, cost = 8900, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 5L, name = "새우", category = "SEA", location = "FREEZER",
                quantity = 3, expirationDate = expiryDateOf(0), dDay = 0, cost = 12000, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 6L, name = "청포도", category = "FRUIT", location = "FRIDGE",
                quantity = 1, expirationDate = expiryDateOf(14), dDay = 14, cost = 4500, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 7L, name = "간장", category = "SAUCE", location = "PANTRY",
                quantity = 1, expirationDate = expiryDateOf(180), dDay = 180, cost = 2800, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 8L, name = "콜라 1.5L", category = "DRINK", location = "PANTRY",
                quantity = 2, expirationDate = expiryDateOf(90), dDay = 90, cost = 1800, createdAt = createdAt(),
            ),
            FridgeIngredientResponse(
                id = 9L, name = "두부", category = "PROCESSED", location = "FRIDGE",
                quantity = 1, expirationDate = expiryDateOf(1), dDay = 1, cost = 1500, createdAt = createdAt(),
            ),
        )
    }
}
