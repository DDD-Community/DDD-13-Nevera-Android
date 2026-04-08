package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult

interface UserRepository {
    suspend fun login(email: String, password: String): ApiResult<LoginResult>
    suspend fun signup(
        email: String,
        password: String,
        passwordCheck: String,
        name: String,
        passwordMatch: Boolean
    ): ApiResult<Unit>
    suspend fun snsLogin(idToken: String): ApiResult<LoginResult>
    suspend fun emailRequest(email: String): ApiResult<Unit>
    suspend fun emailVerify(email: String, authCode: String): ApiResult<Unit>
    suspend fun logout(): ApiResult<Unit>
    suspend fun withdraw(): ApiResult<Unit>
}
