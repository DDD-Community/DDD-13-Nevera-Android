package com.anddd.nevera.feature.ingredient.main

import androidx.lifecycle.SavedStateHandle
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientMutation
import com.anddd.nevera.feature.ingredient.main.model.IngredientSideEffect
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiState
import com.anddd.nevera.feature.ingredient.main.navigation.ARG_IMAGE_URI
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class IngredientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) :
    NeveraViewModel<IngredientUiState, IngredientSideEffect, IngredientIntent, IngredientMutation>(IngredientUiState()) {

    /** 영수증 캡처 이미지 URI — multipart API 호출 시 사용 */
    val imageUri: String = checkNotNull(savedStateHandle[ARG_IMAGE_URI]) {
        "IngredientScreen은 반드시 navigateToIngredient(imageUri)를 통해 진입해야 합니다."
    }

    init {
        handleIntent(IngredientIntent.Load)
    }

    override fun handleIntent(intent: IngredientIntent) {
        when (intent) {
            IngredientIntent.Load -> load()
            IngredientIntent.Submit -> submit()
        }
    }

    private fun load() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: UseCase로 초기 데이터 로드
        applyMutation(IngredientMutation.LoadSuccess)
    }

    private fun submit() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: UseCase로 제출 처리
        postSideEffect(IngredientSideEffect.NavigateBack)
    }

    override suspend fun Syntax<IngredientUiState, IngredientSideEffect>.applyMutation(
        mutation: IngredientMutation,
    ) {
        when (mutation) {
            IngredientMutation.LoadSuccess -> reduce { state.copy(isLoading = false) }
        }
    }
}
