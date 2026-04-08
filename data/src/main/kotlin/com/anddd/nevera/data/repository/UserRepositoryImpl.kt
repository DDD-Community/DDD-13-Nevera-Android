package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.LocalUserDataSource
import com.anddd.nevera.data.datasource.UserDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.LoginType
import com.anddd.nevera.domain.model.SnsProvider
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    // 추후 @RemoteUserDataSource로 교체
    @param:LocalUserDataSource private val userDataSource: UserDataSource,
    private val apiCall: ApiCallExecutor
) : UserRepository {

    // TODO :: 임시
    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<LoginResult> {
        return apiCall { userDataSource.login(email, password) }
            .mapSuccess { it.toDomain(LoginType.EMAIL) }
    }

    // TODO :: 임시
    override suspend fun snsLogin(
        provider: SnsProvider,
        token: String
    ): ApiResult<LoginResult> {
        return apiCall { userDataSource.snsLogin(provider.apiValue, token) }
            .mapSuccess { it.toDomain(LoginType.SNS) }
    }
}