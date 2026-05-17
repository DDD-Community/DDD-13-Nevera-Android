package com.anddd.nevera.feature.mypage.appinfo.model

import com.anddd.nevera.domain.model.appinfo.AppInfo

data class AppInfoUiModel(
    val termsUrl: String = "",
    val privacyPolicyUrl: String = "",
    val versionName: String = "",
)

internal fun AppInfo.toUiModel(): AppInfoUiModel = AppInfoUiModel(
    termsUrl = termsUrl,
    privacyPolicyUrl = privacyPolicyUrl,
    versionName = "V$versionName",
)
