package com.anddd.nevera.domain.usecase.validator

import javax.inject.Inject

class EmailValidator @Inject constructor() {

    private val emailRegex = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    fun validate(email: String): EmailValidationResult = when {
        email.isBlank() -> EmailValidationResult.Empty
        !emailRegex.matches(email) -> EmailValidationResult.InvalidFormat
        else -> EmailValidationResult.Valid
    }
}

sealed interface EmailValidationResult {
    data object Valid : EmailValidationResult
    data object Empty : EmailValidationResult
    data object InvalidFormat : EmailValidationResult
}
