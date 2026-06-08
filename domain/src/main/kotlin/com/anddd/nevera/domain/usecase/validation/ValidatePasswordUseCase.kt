package com.anddd.nevera.domain.usecase.validation

import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): PasswordValidationResult = validate(password)

    private fun validate(password: String): PasswordValidationResult {
        if (password.isBlank()) return PasswordValidationResult.Empty

        val errors = buildList {
            if (password.length < MIN_LENGTH) add(PasswordValidationError.TooShort(MIN_LENGTH))
            if (password.length > MAX_LENGTH) add(PasswordValidationError.TooLong(MAX_LENGTH))
            if (password.none { it.isDigit() }) add(PasswordValidationError.MissingDigit)
            if (password.none { it in SPECIAL_CHARS }) add(PasswordValidationError.MissingSpecialChar)
            if (password.none { it in 'a'..'z' || it in 'A'..'Z' }) add(PasswordValidationError.MissingLetter)
            if (password.any { !isAllowedPasswordChar(it) }) add(PasswordValidationError.ContainsInvalidCharacter)
        }

        return if (errors.isEmpty()) PasswordValidationResult.Valid
        else PasswordValidationResult.Invalid(errors)
    }

    private fun isAllowedPasswordChar(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c.isDigit() || c in SPECIAL_CHARS
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 20
        private const val SPECIAL_CHARS = "!@#\$%^&*()_+-=[]{}|;':\",./<>?"
    }
}
