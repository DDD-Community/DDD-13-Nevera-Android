package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.auth.LogoutError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult

interface UserRepository {
    suspend fun loginWithEmail(email: String, password: String): NeveraResult<LoginResult, LoginError>
    suspend fun signup(
        email: String,
        password: String,
    ): NeveraResult<MessageResult, SignupError>
    suspend fun loginWithGoogle(idToken: String): NeveraResult<LoginResult, GoogleLoginError>
    suspend fun emailRequest(email: String): NeveraResult<MessageResult, EmailRequestError>
    suspend fun emailVerify(email: String, authCode: String): NeveraResult<MessageResult, EmailVerifyError>
    suspend fun logout(): NeveraResult<MessageResult, LogoutError>
    suspend fun withdraw(): NeveraResult<MessageResult, WithdrawError>
}
