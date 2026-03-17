package com.anddd.nevera.feature.login.main.model

import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult

data class LoginUiState(
    val status: LoginStatus = LoginStatus.Idle,
    val emailValidation: EmailValidationResult? = null,
    val passwordValidation: PasswordValidationResult? = null
)

sealed interface LoginStatus {
    data object Idle : LoginStatus
    data object Loading : LoginStatus
    data class Success(val userId: String) : LoginStatus
}
