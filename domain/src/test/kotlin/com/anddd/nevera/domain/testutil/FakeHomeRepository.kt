package com.anddd.nevera.domain.testutil

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * [HomeRepository]의 테스트용 구현.
 *
 * [loadSummaryCount]로 "성공했을 때만 요약을 다시 불러오는가"를 검증한다.
 */
class FakeHomeRepository(
    var loadSummaryResult: NeveraResult<HomeSummary, CommonError> =
        NeveraResult.Success(
            HomeSummary(nickname = "네베라", wish = null, rescuedAmount = 0, disposalAmount = 0),
        ),
) : HomeRepository {

    var loadSummaryCount: Int = 0
        private set

    private val summary = MutableStateFlow<HomeSummary?>(null)

    override suspend fun loadSummary(): NeveraResult<HomeSummary, CommonError> {
        loadSummaryCount++
        val result = loadSummaryResult
        if (result is NeveraResult.Success) summary.value = result.data
        return result
    }

    override fun observeHomeSummary(): Flow<HomeSummary> = summary.filterNotNull()
}
