package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult

interface UserRepository {
    suspend fun loginWithEmail(email: String, password: String): NeveraResult<LoginResult, LoginError>

    /**
     * TODO :: NetworkError는 LoginError 처럼 정의해서 변경 필요
     */
    suspend fun signup(
        email: String,
        password: String,
        name: String
    ): NeveraResult<MessageResult, NetworkError>
    suspend fun loginWithGoogle(idToken: String): NeveraResult<LoginResult, NetworkError>
    suspend fun emailRequest(email: String): NeveraResult<MessageResult, NetworkError>
    suspend fun emailVerify(email: String, authCode: String): NeveraResult<MessageResult, NetworkError>
    suspend fun logout(): NeveraResult<MessageResult, NetworkError>
    suspend fun withdraw(): NeveraResult<MessageResult, WithdrawError>
}
