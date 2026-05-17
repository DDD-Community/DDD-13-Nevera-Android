package com.anddd.nevera.domain.usecase.appinfo

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.appinfo.AppInfo
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.repository.AppInfoRepository
import javax.inject.Inject

class GetAppInfoUseCase @Inject constructor(
    private val appInfoRepository: AppInfoRepository,
) {

    suspend operator fun invoke(): NeveraResult<AppInfo, CommonError> {
        return appInfoRepository.getAppInfo()
    }
}
