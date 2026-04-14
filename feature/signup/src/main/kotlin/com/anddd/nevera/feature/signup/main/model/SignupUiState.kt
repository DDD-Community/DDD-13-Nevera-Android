package com.anddd.nevera.feature.signup.main.model

import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

data class SignupUiState(
    val status: SignupStatus = SignupStatus.Idle,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val authCode: String = "",
    val emailValidation: EmailValidationResult? = null,
    val passwordValidation: PasswordValidationResult? = null,
    val isPasswordMatched: Boolean = false,
    val isEmailRequestSent: Boolean = false,
    val isEmailVerified: Boolean = false
)

sealed interface SignupStatus {
    data object Idle : SignupStatus
    data object Loading : SignupStatus
    data object Success : SignupStatus
}
