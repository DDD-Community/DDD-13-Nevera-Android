package com.anddd.nevera.domain.usecase.home

import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHomeSummaryUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {

    operator fun invoke(): Flow<HomeSummary> = homeRepository.observeHomeSummary()
}
