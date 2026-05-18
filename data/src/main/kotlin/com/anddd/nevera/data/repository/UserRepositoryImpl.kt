package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.UserDataSource
import com.anddd.nevera.data.mapper.error.toLoginError
import com.anddd.nevera.data.mapper.error.toWithdrawError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authDataSource: UserDataSource,
    private val apiCall: ApiCallExecutor
) : UserRepository {

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
        name: String,
    ): NeveraResult<MessageResult, NetworkError> {
        return apiCall { authDataSource.signup(email, password, name) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun loginWithGoogle(idToken: String): NeveraResult<LoginResult, NetworkError> {
        return apiCall { authDataSource.loginWithGoogle(idToken) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun emailRequest(email: String): NeveraResult<MessageResult, NetworkError> {
        return apiCall { authDataSource.emailRequest(email) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun emailVerify(
        email: String,
        authCode: String,
    ): NeveraResult<MessageResult, NetworkError> {
        return apiCall { authDataSource.emailVerify(email, authCode) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun logout(): NeveraResult<MessageResult, NetworkError> {
        return apiCall { authDataSource.logout() }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun withdraw(): NeveraResult<MessageResult, WithdrawError> {
        return apiCall { authDataSource.withdraw() }
            .map(
                transformSuccess = { it.toDomain() },
                transformFailure = { it.toWithdrawError() },
            )
    }
}
