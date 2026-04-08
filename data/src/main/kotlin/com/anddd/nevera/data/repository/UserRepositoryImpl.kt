package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.UserDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authDataSource: UserDataSource,
    private val apiCall: ApiCallExecutor
) : UserRepository {

    override suspend fun login(email: String, password: String): ApiResult<LoginResult> {
        return apiCall { authDataSource.login(email, password) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun signup(
        email: String,
        password: String,
        passwordCheck: String,
        name: String,
        passwordMatch: Boolean
    ): ApiResult<Unit> {
        return apiCall { authDataSource.signup(email, password, passwordCheck, name, passwordMatch) }
            .mapSuccess { Unit }
    }

    override suspend fun snsLogin(idToken: String): ApiResult<LoginResult> {
        return apiCall { authDataSource.snsLogin(idToken) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun emailRequest(email: String): ApiResult<Unit> {
        return apiCall { authDataSource.emailRequest(email) }
            .mapSuccess { Unit }
    }

    override suspend fun emailVerify(email: String, authCode: String): ApiResult<Unit> {
        return apiCall { authDataSource.emailVerify(email, authCode) }
            .mapSuccess { Unit }
    }

    override suspend fun logout(): ApiResult<Unit> {
        return apiCall { authDataSource.logout() }
            .mapSuccess { Unit }
    }

    override suspend fun withdraw(): ApiResult<Unit> {
        return apiCall { authDataSource.withdraw() }
            .mapSuccess { Unit }
    }
}
