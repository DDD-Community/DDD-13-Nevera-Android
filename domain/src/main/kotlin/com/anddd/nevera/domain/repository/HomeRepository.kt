package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary

interface HomeRepository {

    suspend fun getSummary(): NeveraResult<HomeSummary, CommonError>
}
