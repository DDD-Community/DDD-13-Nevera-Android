package com.anddd.nevera.feature.auth.main.model

import com.anddd.nevera.core.mvi.NeveraState
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    val passwordValidation: PasswordValidationResult = PasswordValidationResult.Empty,
) : NeveraState
