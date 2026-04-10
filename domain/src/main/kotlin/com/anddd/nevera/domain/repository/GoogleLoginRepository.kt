package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.ApiResult

interface GoogleLoginRepository {

    suspend fun googleLogin(): ApiResult<Unit>
}
