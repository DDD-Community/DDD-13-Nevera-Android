package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.AppInfoRemoteDataSource
import com.anddd.nevera.data.di.qualifier.VersionName
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.domain.model.appinfo.AppInfo
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.repository.AppInfoRepository
import javax.inject.Inject

internal class AppInfoRepositoryImpl @Inject constructor(
    @param:VersionName private val versionName: String,
    private val appInfoRemoteDataSource: AppInfoRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : AppInfoRepository {

    override suspend fun getAppInfo(): NeveraResult<AppInfo, CommonError> {
        return apiCall { appInfoRemoteDataSource.getAppInfo() }.map(
            transformSuccess = {
                AppInfo(
                    termsUrl = it.termsUrl,
                    privacyPolicyUrl = it.privacyPolicyUrl,
                    versionName = versionName,
                )
            },
            transformFailure = {
                it.toCommonError()
            }
        )
    }
}
