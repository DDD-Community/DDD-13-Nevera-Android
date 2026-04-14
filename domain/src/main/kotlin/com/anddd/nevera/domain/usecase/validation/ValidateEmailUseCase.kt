package com.anddd.nevera.domain.usecase.validation

import com.anddd.nevera.domain.model.validation.EmailValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): EmailValidationResult = validate(email)

    private fun validate(email: String): EmailValidationResult = when {
        email.isBlank() -> EmailValidationResult.Empty
        !EMAIL_REGEX.matches(email) -> EmailValidationResult.InvalidFormat
        else -> EmailValidationResult.Valid
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
    }
}
