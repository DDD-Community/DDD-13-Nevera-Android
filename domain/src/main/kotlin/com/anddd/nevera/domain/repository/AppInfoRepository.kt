package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.appinfo.AppInfo
import com.anddd.nevera.domain.model.common.CommonError

interface AppInfoRepository {
    suspend fun getAppInfo(): NeveraResult<AppInfo, CommonError>
}
