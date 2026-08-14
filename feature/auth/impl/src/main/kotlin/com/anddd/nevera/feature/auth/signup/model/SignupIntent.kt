package com.anddd.nevera.feature.auth.signup.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface SignupIntent : NeveraIntent {
    data class EmailChanged(val email: String) : SignupIntent
    data class PasswordChanged(val password: String) : SignupIntent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignupIntent
    data class AuthCodeChanged(val authCode: String) : SignupIntent
    data object RequestEmailVerification : SignupIntent
    data object VerifyAuthCode : SignupIntent
    data object Signup : SignupIntent
    data object NavigateBack : SignupIntent
}
