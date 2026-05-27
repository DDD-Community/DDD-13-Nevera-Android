package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.AuthRemoteDataSource
import com.anddd.nevera.data.mapper.error.toEmailRequestError
import com.anddd.nevera.data.mapper.error.toEmailVerifyError
import com.anddd.nevera.data.mapper.error.toGoogleLoginError
import com.anddd.nevera.data.mapper.error.toLoginError
import com.anddd.nevera.data.mapper.error.toLogoutError
import com.anddd.nevera.data.mapper.error.toSignupError
import com.anddd.nevera.data.mapper.error.toWithdrawError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.auth.LogoutError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.AuthRepository
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : AuthRepository {

    override suspend fun loginWithEmail(
        email: String,
        password: String,
    ): NeveraResult<LoginResult, LoginError> {
        return apiCall {
            authDataSource.loginWithEmail(email, password)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toLoginError() },
        )
    }

    override suspend fun signup(
        email: String,
        password: String,
    ): NeveraResult<MessageResult, SignupError> {
        return apiCall {
            authDataSource.signup(email, password)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toSignupError() },
        )
    }

    override suspend fun loginWithGoogle(idToken: String): NeveraResult<LoginResult, GoogleLoginError> {
        return apiCall {
            authDataSource.loginWithGoogle(idToken)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toGoogleLoginError() },
        )
    }

    override suspend fun emailRequest(email: String): NeveraResult<MessageResult, EmailRequestError> {
        return apiCall {
            authDataSource.emailRequest(email)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toEmailRequestError() },
        )
    }

    override suspend fun emailVerify(
        email: String,
        authCode: String,
    ): NeveraResult<MessageResult, EmailVerifyError> {
        return apiCall {
            authDataSource.emailVerify(email, authCode)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toEmailVerifyError() },
        )
    }

    override suspend fun logout(): NeveraResult<MessageResult, LogoutError> {
        return apiCall {
            authDataSource.logout()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toLogoutError() },
        )
    }

    override suspend fun withdraw(): NeveraResult<MessageResult, WithdrawError> {
        return apiCall {
            authDataSource.withdraw()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toWithdrawError() },
        )
    }
}
