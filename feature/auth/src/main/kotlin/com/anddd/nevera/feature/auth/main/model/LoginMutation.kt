package com.anddd.nevera.feature.auth.main.model

import com.anddd.nevera.core.mvi.NeveraMutation
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

sealed interface LoginMutation : NeveraMutation {
    data class EmailChanged(val email: String, val validation: EmailValidationResult) : LoginMutation
    data class PasswordChanged(val password: String, val validation: PasswordValidationResult) : LoginMutation
    data class ValidationFailed(
        val emailResult: EmailValidationResult,
        val passwordResult: PasswordValidationResult,
    ) : LoginMutation
    data object Loading : LoginMutation
    data object LoginFailed : LoginMutation
}
