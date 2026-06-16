package com.anddd.nevera.feature.fridge.edit

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.ingredient.EditIngredientInput
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.domain.usecase.ingredient.EditIngredientUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetFridgeIngredientByIdUseCase
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientIntent
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientMutation
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientSideEffect
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientUiState
import com.anddd.nevera.feature.fridge.navigation.EditFridgeIngredientRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditFridgeIngredientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFridgeIngredientById: GetFridgeIngredientByIdUseCase,
    private val editIngredient: EditIngredientUseCase,
) : NeveraViewModel<EditFridgeIngredientUiState, EditFridgeIngredientSideEffect, EditFridgeIngredientIntent, EditFridgeIngredientMutation>(
    EditFridgeIngredientUiState()
) {

    private val ingredientId: Long = savedStateHandle.toRoute<EditFridgeIngredientRoute>().ingredientId

    init {
        intent { loadIngredient() }
    }

    override fun handleIntent(intent: EditFridgeIngredientIntent) {
        when (intent) {
            is EditFridgeIngredientIntent.UpdateQuantity -> onUpdateQuantity(intent.quantity)
            is EditFridgeIngredientIntent.UpdateCost -> onUpdateCost(intent.cost)
            is EditFridgeIngredientIntent.UpdateCategory -> onUpdateCategory(intent.category)
            is EditFridgeIngredientIntent.UpdateStorageLocation -> onUpdateStorageLocation(intent.location)
            is EditFridgeIngredientIntent.UpdateExpiryDate -> onUpdateExpiryDate(intent.date)
            EditFridgeIngredientIntent.ConfirmClick -> onConfirmClick()
            EditFridgeIngredientIntent.CloseClick -> intent { postSideEffect(EditFridgeIngredientSideEffect.NavigateBack) }
            EditFridgeIngredientIntent.CategoryFieldClick -> intent { postSideEffect(EditFridgeIngredientSideEffect.ShowCategorySheet) }
            EditFridgeIngredientIntent.StorageLocationFieldClick -> intent { postSideEffect(EditFridgeIngredientSideEffect.ShowStorageLocationSheet) }
            EditFridgeIngredientIntent.ExpiryDateFieldClick -> intent { postSideEffect(EditFridgeIngredientSideEffect.ShowDatePicker) }
        }
    }

    private suspend fun Syntax<EditFridgeIngredientUiState, EditFridgeIngredientSideEffect>.loadIngredient() {
        applyMutation(EditFridgeIngredientMutation.Loading)
        getFridgeIngredientById(ingredientId)
            .onSuccess { ingredient ->
                applyMutation(
                    EditFridgeIngredientMutation.Loaded(
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        cost = ingredient.cost,
                        category = ingredient.category,
                        storageLocation = ingredient.storageLocation,
                        expiryDate = ingredient.expiryDate,
                    )
                )
            }
            .onFailure {
                postSideEffect(EditFridgeIngredientSideEffect.NavigateBack)
            }
    }

    private fun onUpdateQuantity(quantity: Int) = intent {
        applyMutation(EditFridgeIngredientMutation.QuantityUpdated(quantity))
    }

    private fun onUpdateCost(cost: Int) = intent {
        applyMutation(EditFridgeIngredientMutation.CostUpdated(cost))
    }

    private fun onUpdateCategory(category: FoodCategory) = intent {
        applyMutation(EditFridgeIngredientMutation.CategoryUpdated(category))
    }

    private fun onUpdateStorageLocation(location: StorageLocation) = intent {
        applyMutation(EditFridgeIngredientMutation.StorageLocationUpdated(location))
    }

    private fun onUpdateExpiryDate(date: LocalDate) = intent {
        applyMutation(EditFridgeIngredientMutation.ExpiryDateUpdated(date))
    }

    private fun onConfirmClick() = intent {
        applyMutation(EditFridgeIngredientMutation.Loading)
        val input = EditIngredientInput(
            name = state.name,
            category = state.category,
            location = state.storageLocation,
            quantity = state.quantity,
            expiryDate = state.expiryDate,
            cost = state.cost,
        )
        editIngredient(ingredientId, input)
            .onSuccess {
                applyMutation(EditFridgeIngredientMutation.UpdateComplete)
                postSideEffect(EditFridgeIngredientSideEffect.NavigateBack)
            }
            .onFailure {
                applyMutation(EditFridgeIngredientMutation.UpdateComplete)
                postSideEffect(EditFridgeIngredientSideEffect.ShowUpdateFailedToast)
            }
    }

    override suspend fun Syntax<EditFridgeIngredientUiState, EditFridgeIngredientSideEffect>.applyMutation(
        mutation: EditFridgeIngredientMutation,
    ) {
        when (mutation) {
            EditFridgeIngredientMutation.Loading -> reduce { state.copy(isLoading = true) }
            EditFridgeIngredientMutation.UpdateComplete -> reduce { state.copy(isLoading = false) }
            is EditFridgeIngredientMutation.Loaded -> reduce {
                state.copy(
                    isLoading = false,
                    name = mutation.name,
                    quantity = mutation.quantity,
                    cost = mutation.cost,
                    category = mutation.category,
                    storageLocation = mutation.storageLocation,
                    expiryDate = mutation.expiryDate,
                )
            }
            is EditFridgeIngredientMutation.QuantityUpdated -> reduce { state.copy(quantity = mutation.quantity) }
            is EditFridgeIngredientMutation.CostUpdated -> reduce { state.copy(cost = mutation.cost) }
            is EditFridgeIngredientMutation.CategoryUpdated -> reduce { state.copy(category = mutation.category) }
            is EditFridgeIngredientMutation.StorageLocationUpdated -> reduce { state.copy(storageLocation = mutation.location) }
            is EditFridgeIngredientMutation.ExpiryDateUpdated -> reduce { state.copy(expiryDate = mutation.date) }
        }
    }
}
