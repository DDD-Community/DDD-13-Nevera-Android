package com.anddd.nevera.data.testutil

import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.ProcessIngredientResponse
import com.anddd.nevera.data.model.ingredient.IngredientResponse

/**
 * 식재료·냉장고 도메인의 응답 픽스처.
 *
 * [fridgeIngredientResponse]는 재료 수정과 냉장고 단건 조회가 공유하는 [FridgeIngredientResponse]를
 * 만든다. 만드는 DTO를 기준으로 이 도메인 파일에 둔다(호출하는 대역이 여럿이라 소비자 기준으로는
 * 소유가 정해지지 않는다).
 */
internal fun fridgeIngredientResponse(
    id: Long = 1L,
    name: String = "당근",
    category: String = "VEG",
    location: String = "FRIDGE",
    quantity: Int = 1,
    cost: Int = 3_000,
) = FridgeIngredientResponse(
    id = id,
    name = name,
    category = category,
    location = location,
    quantity = quantity,
    expirationDate = "2026-12-31T00:00:00+09:00",
    cost = cost,
    createdAt = "2026-07-22T09:30:00+09:00",
)

internal fun ingredientResponse(
    id: Long = 1L,
    name: String = "당근",
    category: String = "VEG",
) = IngredientResponse(
    id = id,
    name = name,
    category = category,
    categoryDisplayName = "채소",
    quantity = 1,
    cost = 3_000,
)

internal fun processIngredientResponse(
    inventoryId: Long = 1L,
    completed: Boolean = false,
    remainingAmount: Int = 1_500,
) = ProcessIngredientResponse(
    inventoryId = inventoryId,
    processedStatus = "CONSUMED",
    processedRatio = 50,
    processedAmount = 1_500,
    consumedRatio = 50,
    wastedRatio = 0,
    remainingRatio = 50,
    remainingAmount = remainingAmount,
    inventoryStatus = "ACTIVE",
    completed = completed,
)
