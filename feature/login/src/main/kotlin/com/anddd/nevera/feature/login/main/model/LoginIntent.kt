package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface LoginIntent : NeveraIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object LoginWithEmailClicked : LoginIntent
    data object GoogleLoginButtonClicked : LoginIntent
    data object SignupClicked : LoginIntent
    data class GoogleLoginSucceeded(val token: String) : LoginIntent
    data object GoogleLoginFailed : LoginIntent
}
