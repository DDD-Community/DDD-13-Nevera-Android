package com.anddd.nevera.domain.usecase.home

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeSummaryUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {

    suspend operator fun invoke(): NeveraResult<HomeSummary, CommonError> {
        return homeRepository.getSummary()
    }
}
