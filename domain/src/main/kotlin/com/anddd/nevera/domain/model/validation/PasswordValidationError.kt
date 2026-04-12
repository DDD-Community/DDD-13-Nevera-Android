package com.anddd.nevera.domain.model.validation

sealed interface PasswordValidationError {
    data class TooShort(val minLength: Int) : PasswordValidationError
    data object MissingUppercase : PasswordValidationError
    data object MissingLowercase : PasswordValidationError
    data object MissingDigit : PasswordValidationError
    data object MissingSpecialChar : PasswordValidationError
}
