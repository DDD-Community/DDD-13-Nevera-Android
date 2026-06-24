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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

internal class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : HomeRepository {

    private val _homeSummary = MutableStateFlow<HomeSummary?>(null)

    override suspend fun loadSummary(): NeveraResult<HomeSummary, CommonError> =
        apiCall {
            homeRemoteDataSource.getSummary()
        }.map(
            transformSuccess = { response ->
                val summary = response.toDomain()
                _homeSummary.value = summary
                summary
            },
            transformFailure = { it.toCommonError() },
        )

    override fun observeHomeSummary(): Flow<HomeSummary> = _homeSummary.filterNotNull()
}
