package com.anddd.nevera.feature.login.main.model

sealed interface LoginSideEffect {
    data class ShowErrorToast(val message: String) : LoginSideEffect

    data object NavigateToMain : LoginSideEffect
}
