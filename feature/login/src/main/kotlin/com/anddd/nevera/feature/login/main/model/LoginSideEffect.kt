package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface LoginSideEffect : NeveraSideEffect {
    data class EmailLoginFailed(val error: EmailLoginErrorUiModel) : LoginSideEffect
    data class GoogleLoginFailed(val error: GoogleLoginErrorUiModel) : LoginSideEffect
    data object MoveToHomeScreen : LoginSideEffect
    data object MoveToSignupScreen : LoginSideEffect
    data object StartGoogleLogin : LoginSideEffect
}
