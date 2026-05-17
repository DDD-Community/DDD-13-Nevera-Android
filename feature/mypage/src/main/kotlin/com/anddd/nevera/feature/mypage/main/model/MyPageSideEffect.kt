package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface MyPageSideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : MyPageSideEffect
    data object NavigateToAppInfo : MyPageSideEffect
}
