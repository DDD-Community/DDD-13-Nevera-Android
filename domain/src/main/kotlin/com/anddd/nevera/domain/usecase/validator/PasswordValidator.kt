package com.anddd.nevera.domain.usecase.validator

import javax.inject.Inject

class PasswordValidator @Inject constructor() {

    fun validate(password: String): PasswordValidationResult {
        if (password.isBlank()) return PasswordValidationResult.Empty

        val errors = buildList {
            if (password.length < MIN_LENGTH) add(PasswordValidationError.TooShort(MIN_LENGTH))
            if (password.none { it.isUpperCase() }) add(PasswordValidationError.MissingUppercase)
            if (password.none { it.isLowerCase() }) add(PasswordValidationError.MissingLowercase)
            if (password.none { it.isDigit() }) add(PasswordValidationError.MissingDigit)
            if (password.none { it in SPECIAL_CHARS }) add(PasswordValidationError.MissingSpecialChar)
        }

        return if (errors.isEmpty()) PasswordValidationResult.Valid
               else PasswordValidationResult.Invalid(errors)
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val SPECIAL_CHARS = "!@#\$%^&*()_+\\-=\\[\\]{}|;':\",./<>?"
    }
}

sealed interface PasswordValidationResult {
    data object Valid : PasswordValidationResult
    data object Empty : PasswordValidationResult
    data class Invalid(val errors: List<PasswordValidationError>) : PasswordValidationResult
}

sealed interface PasswordValidationError {
    data class TooShort(val minLength: Int) : PasswordValidationError
    data object MissingUppercase : PasswordValidationError
    data object MissingLowercase : PasswordValidationError
    data object MissingDigit : PasswordValidationError
    data object MissingSpecialChar : PasswordValidationError
}
