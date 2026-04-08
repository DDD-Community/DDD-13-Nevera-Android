package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.common.ApiResponse
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.LoginRequest
import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.SnsLoginRequest
import javax.inject.Inject

internal class RemoteUserDataSourceImpl @Inject constructor(
    private val userApi: UserApi
) : UserDataSource {

    override suspend fun login(email: String, password: String): ApiResponse<LoginResponse> =
        userApi.login(LoginRequest(email, password))

    override suspend fun snsLogin(provider: String, token: String): ApiResponse<LoginResponse> =
        userApi.snsLogin(SnsLoginRequest(provider, token))
}
