package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.network.apiCall
import com.anddd.nevera.data.datasource.LocalUserDataSource
import com.anddd.nevera.data.datasource.UserDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.LoginType
import com.anddd.nevera.domain.model.SnsProvider
import com.anddd.nevera.domain.model.User
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    // 추후 @RemoteUserDataSource로 교체
    @param:LocalUserDataSource private val userDataSource: UserDataSource
) : UserRepository {

    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<LoginResult> {
        return when (val result = apiCall { userDataSource.login(email, password) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(LoginType.EMAIL))
            is ApiResult.Error -> result
        }
    }

    override suspend fun snsLogin(
        provider: SnsProvider,
        token: String
    ): ApiResult<LoginResult> {
        return when (val result = apiCall { userDataSource.snsLogin(provider.apiValue, token) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(LoginType.SNS))
            is ApiResult.Error -> result
        }
    }

    override suspend fun getUser(userId: String): ApiResult<User> {
        return when (val result = apiCall { userDataSource.getUser(userId) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
        }
    }
}
