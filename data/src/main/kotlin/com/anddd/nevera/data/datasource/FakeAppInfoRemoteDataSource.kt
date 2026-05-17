package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.appinfo.AppInfoResponse
import javax.inject.Inject

internal class FakeAppInfoRemoteDataSource @Inject constructor() : AppInfoRemoteDataSource {

    override suspend fun getAppInfo(): ApiResponse<AppInfoResponse> {
        return ApiResponse(
            result = AppInfoResponse(
                termsUrl = "https://sikgu.notion.site/35a2b85edd1f8018a836c7db401110f4",
                privacyPolicyUrl = "https://sikgu.notion.site/35a2b85edd1f80828c2aea00237b6fa6",
            ),
            error = null
        )
    }
}
