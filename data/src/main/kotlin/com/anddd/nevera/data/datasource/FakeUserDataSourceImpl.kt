package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.common.ApiResponse
import com.anddd.nevera.core.common.ApiError
import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.UserResponse
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class FakeUserDataSourceImpl @Inject constructor() : UserDataSource {

    override suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        delay(500)
        if (email.isBlank() || password.isBlank()) {
            return ApiResponse(result = null, error = ApiError(code = 400, message = "이메일 또는 비밀번호가 올바르지 않습니다."))
        }
        val user = UserResponse(id = "user_001", name = "홍길동", email = email)
        return ApiResponse(result = LoginResponse(user = user, accessToken = "fake_token_abc123", refreshToken = "fake_refresh_abc123"), error = null)
    }

    override suspend fun snsLogin(provider: String, token: String): ApiResponse<LoginResponse> {
        delay(500)
        val user = UserResponse(id = "user_sns_001", name = "홍길동(SNS)", email = "sns_user@example.com")
        return ApiResponse(result = LoginResponse(user = user, accessToken = "fake_sns_token_xyz789", refreshToken = "fake_sns_refresh_xyz789"), error = null)
    }

    override suspend fun getUser(userId: String): ApiResponse<UserResponse> {
        delay(300)
        return ApiResponse(result = UserResponse(id = userId, name = "홍길동", email = "user@example.com"), error = null)
    }
}
