package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.SnsProvider

interface UserRepository {
    suspend fun login(email: String, password: String): ApiResult<LoginResult>
    suspend fun snsLogin(provider: SnsProvider, token: String): ApiResult<LoginResult>
}
