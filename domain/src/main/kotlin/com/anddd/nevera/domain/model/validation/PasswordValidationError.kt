package com.anddd.nevera.domain.model.validation

sealed interface PasswordValidationError {
    data class TooShort(val minLength: Int) : PasswordValidationError
    data class TooLong(val maxLength: Int) : PasswordValidationError
    data object ContainsInvalidCharacter : PasswordValidationError
    data object MissingLetter : PasswordValidationError
    data object MissingDigit : PasswordValidationError
    data object MissingSpecialChar : PasswordValidationError
}
