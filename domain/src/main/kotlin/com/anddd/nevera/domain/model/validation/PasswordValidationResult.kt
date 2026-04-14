package com.anddd.nevera.domain.model.validation

sealed interface PasswordValidationResult {
    data object Valid : PasswordValidationResult
    data object Empty : PasswordValidationResult
    data class Invalid(val errors: List<PasswordValidationError>) : PasswordValidationResult
}
