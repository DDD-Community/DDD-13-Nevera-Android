package com.anddd.nevera.data.datasource

import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.UserResponse

internal interface UserDataSource {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun snsLogin(provider: String, token: String): LoginResponse
    suspend fun getUser(userId: String): UserResponse
}
