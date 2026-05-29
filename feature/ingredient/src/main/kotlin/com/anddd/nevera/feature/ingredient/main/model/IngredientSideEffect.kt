package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface IngredientSideEffect : NeveraSideEffect {
    data object NavigateToOcrError        : IngredientSideEffect
    data class  NavigateToSuccess(val totalCost: Int) : IngredientSideEffect
    /** 등록 실패 Toast — 실제 메시지 문자열은 UI에서 stringResource로 참조 */
    data object ShowRegisterFailedToast   : IngredientSideEffect
    data object NavigateBack              : IngredientSideEffect
}