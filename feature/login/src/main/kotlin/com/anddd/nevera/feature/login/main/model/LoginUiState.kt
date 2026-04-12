package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

data class LoginUiState(
    val status: LoginStatus = LoginStatus.Idle,
    val email: String = "",
    val password: String = "",
    val emailValidation: EmailValidationResult? = null,
    val passwordValidation: PasswordValidationResult? = null
)

sealed interface LoginStatus {
    data object Idle : LoginStatus
    data object Loading : LoginStatus
    data object Success : LoginStatus
}
