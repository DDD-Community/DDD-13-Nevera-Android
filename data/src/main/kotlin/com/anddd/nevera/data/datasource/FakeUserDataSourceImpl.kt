package com.anddd.nevera.data.datasource

import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.UserResponse
import kotlinx.coroutines.delay
import javax.inject.Inject

internal class FakeUserDataSourceImpl @Inject constructor() : UserDataSource {

    override suspend fun login(email: String, password: String): LoginResponse {
        delay(500)
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }
        val user = UserResponse(id = "user_001", name = "홍길동", email = email)
        return LoginResponse(user = user, token = "fake_token_abc123")
    }

    override suspend fun snsLogin(provider: String, token: String): LoginResponse {
        delay(500)
        val user = UserResponse(id = "user_sns_001", name = "홍길동(SNS)", email = "sns_user@example.com")
        return LoginResponse(user = user, token = "fake_sns_token_xyz789")
    }

    override suspend fun getUser(userId: String): UserResponse {
        delay(300)
        return UserResponse(id = userId, name = "홍길동", email = "user@example.com")
    }
}
