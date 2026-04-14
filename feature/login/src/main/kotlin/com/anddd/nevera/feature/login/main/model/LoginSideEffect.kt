package com.anddd.nevera.feature.login.main.model

sealed interface LoginSideEffect {
    data class ShowToast(val message: String) : LoginSideEffect
    data class ShowErrorToast(val message: String) : LoginSideEffect
    object MoveToHomeScreen: LoginSideEffect
}
