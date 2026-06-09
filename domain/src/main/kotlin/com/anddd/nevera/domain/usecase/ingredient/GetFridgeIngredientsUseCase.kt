package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.domain.repository.FridgeRepository
import javax.inject.Inject

class GetFridgeIngredientsUseCase @Inject constructor(
    private val fridgeRepository: FridgeRepository,
) {

    suspend operator fun invoke(
        storageLocation: StorageLocation?,
        category: FoodCategory?,
        sortOrder: IngredientSortOrder = IngredientSortOrder.ExpiryDate,
    ): NeveraResult<List<FridgeIngredient>, CommonError> =
        fridgeRepository.getFridgeIngredients(
            storageLocation = storageLocation,
            category = category,
            sortOrder = sortOrder,
            page = 0,
            size = 100,
        )
}
