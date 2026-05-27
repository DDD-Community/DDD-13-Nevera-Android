package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.home.HomeSummaryResponse

internal interface HomeRemoteDataSource {
    suspend fun getSummary(): ApiResponse<HomeSummaryResponse>
}
