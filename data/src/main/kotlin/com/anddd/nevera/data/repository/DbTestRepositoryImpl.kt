package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.DbTestDataSource
import com.anddd.nevera.data.datasource.RemoteDbTestDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.DbTest
import com.anddd.nevera.domain.repository.DbTestRepository
import javax.inject.Inject

internal class DbTestRepositoryImpl @Inject constructor(
    @param:RemoteDbTestDataSource private val dbTestDataSource: DbTestDataSource,
    private val apiCall: ApiCallExecutor
) : DbTestRepository {

    override suspend fun getDbTest(): ApiResult<DbTest> {
        return apiCall { dbTestDataSource.getDbTest() }
            .mapSuccess { it.toDomain() }
    }
}