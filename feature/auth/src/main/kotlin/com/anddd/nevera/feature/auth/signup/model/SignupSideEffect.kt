package com.anddd.nevera.feature.auth.signup.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface SignupSideEffect : NeveraSideEffect {
    data class EmailRequestDuplicateEmail(val message: String?) : SignupSideEffect
    data class EmailRequestMailSendError(val message: String?) : SignupSideEffect
    data class EmailRequestNetworkError(val message: String?) : SignupSideEffect
    data class EmailVerifyNotFound(val message: String?) : SignupSideEffect
    data object SignupEmailNotVerified : SignupSideEffect
    data class SignupUnverifiedEmail(val message: String?) : SignupSideEffect
    data class SignupAuthNotFound(val message: String?) : SignupSideEffect
    data object SignupServerError : SignupSideEffect
    data object TimerExpired : SignupSideEffect
    data object MoveToLoginScreen : SignupSideEffect
}
