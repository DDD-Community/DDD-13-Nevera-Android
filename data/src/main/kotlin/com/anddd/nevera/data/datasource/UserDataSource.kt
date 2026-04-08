package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.common.ApiResponse
import com.anddd.nevera.data.model.LoginResponse

internal interface UserDataSource {
    suspend fun login(email: String, password: String): ApiResponse<LoginResponse>
    suspend fun snsLogin(provider: String, token: String): ApiResponse<LoginResponse>
}
