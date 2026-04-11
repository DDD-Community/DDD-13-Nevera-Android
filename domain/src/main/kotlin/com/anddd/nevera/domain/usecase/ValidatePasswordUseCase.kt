package com.anddd.nevera.domain.usecase

import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidator
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor(
    private val passwordValidator: PasswordValidator
) {
    operator fun invoke(password: String): PasswordValidationResult =
        passwordValidator.validate(password)
}
