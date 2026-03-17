package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.SnsProvider
import com.anddd.nevera.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): ApiResult<LoginResult>
    suspend fun snsLogin(provider: SnsProvider, token: String): ApiResult<LoginResult>
    suspend fun getUser(userId: String): ApiResult<User>
}
