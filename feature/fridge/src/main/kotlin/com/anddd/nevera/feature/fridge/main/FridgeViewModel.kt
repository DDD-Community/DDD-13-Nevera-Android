package com.anddd.nevera.feature.fridge.main

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeMutation
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.CategoryFilter
import com.anddd.nevera.feature.fridge.main.model.IngredientSortOrder
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor() :
    NeveraViewModel<FridgeUiState, FridgeSideEffect, FridgeIntent, FridgeMutation>(FridgeUiState()) {

    init {
        handleIntent(FridgeIntent.Load)
    }

    override fun handleIntent(intent: FridgeIntent) {
        when (intent) {
            FridgeIntent.Load -> load()

            is FridgeIntent.SelectStorageFilter -> selectStorageFilter(intent.filter)

            is FridgeIntent.SelectCategoryFilter -> selectCategoryFilter(intent.filter)

            FridgeIntent.AddIngredientClick -> addIngredient()

            is FridgeIntent.SelectSortOrder -> selectSortOrder(intent.order)
        }
    }

    private fun load() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: UseCase로 초기 데이터 로드
        applyMutation(FridgeMutation.LoadSuccess)
    }

    private fun selectStorageFilter(filter: StorageLocationFilter) = intent {
        applyMutation(FridgeMutation.SelectStorageFilter(filter))
    }

    private fun addIngredient() = intent {
        postSideEffect(FridgeSideEffect.ShowCaptureModeBottomSheet)
    }

    private fun selectSortOrder(order: IngredientSortOrder) = intent {
        applyMutation(FridgeMutation.SelectSortOrder(order))
    }

    private fun selectCategoryFilter(filter: CategoryFilter) = intent {
        applyMutation(
            FridgeMutation.SelectCategoryFilter(
                storageFilter = state.selectedStorageFilter,
                categoryFilter = filter,
            )
        )
    }

    override suspend fun Syntax<FridgeUiState, FridgeSideEffect>.applyMutation(
        mutation: FridgeMutation,
    ) {
        when (mutation) {
            FridgeMutation.LoadSuccess -> reduce { state.copy(isLoading = false) }

            is FridgeMutation.SelectStorageFilter -> reduce { state.copy(selectedStorageFilter = mutation.filter) }

            is FridgeMutation.SelectCategoryFilter -> reduce {
                state.copy(
                    categoryFilters = state.categoryFilters + (mutation.storageFilter to mutation.categoryFilter),
                )
            }

            is FridgeMutation.SelectSortOrder -> reduce { state.copy(selectedSortOrder = mutation.order) }
        }
    }
}
