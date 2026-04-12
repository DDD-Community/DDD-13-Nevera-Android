package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.UserDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authDataSource: UserDataSource,
    private val apiCall: ApiCallExecutor
) : UserRepository {

    override suspend fun loginWithEmail(email: String, password: String): NeveraResult<LoginResult, LoginError> {
        return when (val result = apiCall { authDataSource.loginWithEmail(email, password) }) {
            is ApiResult.Success -> NeveraResult.Success(result.data.toDomain())
            is ApiResult.Error -> NeveraResult.Failure(result.error.toLoginError())
        }
    }

    // NetworkError → CommonError 변환
    // 판단 기준: HTTP 없이도 의미 있는 개념만 domain 에러로 표현한다.
    private fun NetworkError.toCommonError(): CommonError = when (this) {
        is NetworkError.HttpError -> CommonError.ServerError(message)
        is NetworkError.NetworkConnectionError -> CommonError.NetworkUnavailable
        is NetworkError.TimeoutError -> CommonError.Timeout
        is NetworkError.UnknownError -> CommonError.Unknown
    }

    // NetworkError → LoginError 변환
    // TODO: 서버 비즈니스 에러 코드 확정 후 InvalidCredentials 매핑 추가
    //       e.g. is NetworkError.HttpError -> if (code == SERVER_CODE_INVALID_CREDENTIALS) LoginError.InvalidCredentials
    private fun NetworkError.toLoginError(): LoginError = when (this) {
        is NetworkError.HttpError -> when (code) {
            401 -> LoginError.Common(CommonError.Unauthorized)
            else -> LoginError.Common(toCommonError())
        }
        else -> LoginError.Common(toCommonError())
    }

    override suspend fun signup(
        email: String,
        password: String,
        name: String
    ): ApiResult<MessageResult> {
        return apiCall { authDataSource.signup(email, password, name) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun loginWithGoogle(idToken: String): ApiResult<LoginResult> {
        return apiCall { authDataSource.loginWithGoogle(idToken) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun emailRequest(email: String): ApiResult<MessageResult> {
        return apiCall { authDataSource.emailRequest(email) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun emailVerify(email: String, authCode: String): ApiResult<MessageResult> {
        return apiCall { authDataSource.emailVerify(email, authCode) }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun logout(): ApiResult<MessageResult> {
        return apiCall { authDataSource.logout() }
            .mapSuccess { it.toDomain() }
    }

    override suspend fun withdraw(): ApiResult<MessageResult> {
        return apiCall { authDataSource.withdraw() }
            .mapSuccess { it.toDomain() }
    }
}
