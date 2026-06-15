package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime

internal fun IngredientSortOrder.toApiString(): String = when (this) {
    IngredientSortOrder.ExpiryDate -> "EXPIRY_DATE"
    IngredientSortOrder.Latest -> "LATEST"
}

internal fun FridgeIngredientResponse.toDomain(): FridgeIngredient = FridgeIngredient(
    id = id,
    name = name,
    category = category.toFoodCategory(),
    storageLocation = location.toStorageLocation(),
    quantity = quantity,
    cost = cost,
    expiryDate = runCatching { OffsetDateTime.parse(expirationDate).toLocalDate() }
        .getOrElse { LocalDate.now() },
    createdAt = runCatching { OffsetDateTime.parse(createdAt).toInstant() }
        .getOrElse { Instant.now() },
)
