package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.UserRemoteDataSource
import com.anddd.nevera.data.mapper.error.toProfileError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.ProfileError
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : UserRepository {

    override suspend fun getProfile(): NeveraResult<Profile, ProfileError> {
        return apiCall {
            userRemoteDataSource.getProfile()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toProfileError() },
        )
    }
}
