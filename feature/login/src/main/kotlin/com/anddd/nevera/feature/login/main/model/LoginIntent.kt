package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface LoginIntent : NeveraIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object LoginWithEmailClicked : LoginIntent
    data class LoginWithGoogleClicked(val token: String) : LoginIntent
    data class GoogleLoginFailed(val throwable: Throwable) : LoginIntent
}
