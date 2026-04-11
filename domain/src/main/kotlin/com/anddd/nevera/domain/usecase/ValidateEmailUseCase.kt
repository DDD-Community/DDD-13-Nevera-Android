package com.anddd.nevera.domain.usecase

import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.EmailValidator
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val emailValidator: EmailValidator
) {
    operator fun invoke(email: String): EmailValidationResult =
        emailValidator.validate(email)
}
