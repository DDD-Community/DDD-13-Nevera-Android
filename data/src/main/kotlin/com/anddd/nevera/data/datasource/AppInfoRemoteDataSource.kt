package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.appinfo.AppInfoResponse

internal interface AppInfoRemoteDataSource {
    suspend fun getAppInfo(): ApiResponse<AppInfoResponse>
}
