package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.HomeRemoteDataSource
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.repository.HomeRepository
import javax.inject.Inject

internal class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : HomeRepository {

    override suspend fun getSummary(): NeveraResult<HomeSummary, CommonError> {
        return apiCall {
            homeRemoteDataSource.getSummary()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toCommonError() },
        )
    }
}
