package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.network.apiCall
import com.anddd.nevera.data.datasource.DbTestDataSource
import com.anddd.nevera.data.datasource.RemoteDbTestDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.DbTest
import com.anddd.nevera.domain.repository.DbTestRepository
import com.google.gson.Gson
import javax.inject.Inject

internal class DbTestRepositoryImpl @Inject constructor(
    @param:RemoteDbTestDataSource private val dbTestDataSource: DbTestDataSource,
    private val gson: Gson
) : DbTestRepository {

    override suspend fun getDbTest(): ApiResult<DbTest> {
        return when (val result = apiCall(gson) { dbTestDataSource.getDbTest() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
        }
    }
}
