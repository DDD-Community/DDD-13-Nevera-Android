package com.anddd.nevera.data.datasource

import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.LoginRequest
import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.SnsLoginRequest
import com.anddd.nevera.data.model.UserResponse
import javax.inject.Inject

internal class RemoteUserDataSourceImpl @Inject constructor(
    private val userApi: UserApi
) : UserDataSource {

    override suspend fun login(email: String, password: String): LoginResponse {
        return userApi.login(LoginRequest(email, password))
    }

    override suspend fun snsLogin(provider: String, token: String): LoginResponse {
        return userApi.snsLogin(SnsLoginRequest(provider, token))
    }

    override suspend fun getUser(userId: String): UserResponse {
        return userApi.getUser(userId)
    }
}
