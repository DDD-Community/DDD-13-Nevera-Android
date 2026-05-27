package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.HomeApi
import com.anddd.nevera.data.model.home.HomeSummaryResponse
import javax.inject.Inject

internal class HomeRemoteDataSourceImpl @Inject constructor(
    private val homeApi: HomeApi,
) : HomeRemoteDataSource {

    override suspend fun getSummary(): ApiResponse<HomeSummaryResponse> {
        return homeApi.getSummary()
    }
}
