package com.anddd.nevera.feature.signup.main.model

sealed interface SignupSideEffect {
    data class ShowToast(val message: String) : SignupSideEffect
    data object MoveToLoginScreen : SignupSideEffect
}
