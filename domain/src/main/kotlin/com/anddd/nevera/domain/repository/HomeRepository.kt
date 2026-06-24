package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    /** 서버에서 홈 요약 데이터를 가져와 내부 캐시를 갱신한다. */
    suspend fun loadSummary(): NeveraResult<HomeSummary, CommonError>

    /** 내부 캐시로부터 홈 요약 데이터를 관찰한다. 데이터가 로드되기 전까지 emit하지 않는다. */
    fun observeHomeSummary(): Flow<HomeSummary>
}
