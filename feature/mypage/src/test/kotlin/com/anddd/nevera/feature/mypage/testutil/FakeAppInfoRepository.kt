package com.anddd.nevera.feature.mypage.testutil

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.appinfo.AppInfo
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.repository.AppInfoRepository

/**
 * [AppInfoRepository]의 테스트용 구현.
 *
 * ViewModel은 UseCase에 의존하고 UseCase는 클래스라 대역으로 바꿀 수 없으므로,
 * 그 아래 저장소 인터페이스를 대역으로 바꾸고 실제 UseCase를 그대로 쓴다.
 */
class FakeAppInfoRepository(
    var result: NeveraResult<AppInfo, CommonError> = NeveraResult.Success(
        AppInfo(
            termsUrl = "https://nevera.example.com/terms",
            privacyPolicyUrl = "https://nevera.example.com/privacy",
            versionName = "1.2.3",
        ),
    ),
) : AppInfoRepository {

    override suspend fun getAppInfo(): NeveraResult<AppInfo, CommonError> = result
}
