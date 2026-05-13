package com.anddd.nevera.feature.mypage.main.model

sealed interface MyPageSideEffect {
    data class ShowToast(val message: String) : MyPageSideEffect
    data object NavigateBack : MyPageSideEffect
}
