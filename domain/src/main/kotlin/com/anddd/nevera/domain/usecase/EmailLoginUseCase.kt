package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.EmailValidator
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidator
import javax.inject.Inject

class EmailLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator
) {

    suspend operator fun invoke(email: String, password: String): ApiResult<LoginResult> {
        val result = userRepository.login(email, password)
        if (result is ApiResult.Success) {
            tokenRepository.setTokens(result.data.token, result.data.refreshToken)
        }
        return result
    }

    fun validateEmail(email: String): EmailValidationResult =
        emailValidator.validate(email)

    fun validatePassword(password: String): PasswordValidationResult =
        passwordValidator.validate(password)
}
