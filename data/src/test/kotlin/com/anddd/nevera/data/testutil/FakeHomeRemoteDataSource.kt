package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.datasource.HomeRemoteDataSource
import com.anddd.nevera.data.model.home.HomeSummaryResponse

/**
 * [HomeRemoteDataSource]의 테스트용 대역.
 *
 * 서버 호출을 흉내 내므로 미리 정해 둔 [ApiResponse]를 그대로 돌려준다.
 * 서버가 200과 함께 에러 본문을 내려주는 상황은 `result = null, error = ApiError(...)`로 표현한다.
 */
internal class FakeHomeRemoteDataSource(
    var summaryResponse: ApiResponse<HomeSummaryResponse> = ApiResponse(
        result = homeSummaryResponse(),
        error = null,
    ),
) : HomeRemoteDataSource {

    var getSummaryCount: Int = 0
        private set

    override suspend fun getSummary(): ApiResponse<HomeSummaryResponse> {
        getSummaryCount++
        return summaryResponse
    }
}
