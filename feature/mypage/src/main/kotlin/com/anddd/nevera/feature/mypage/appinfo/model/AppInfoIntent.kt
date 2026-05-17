package com.anddd.nevera.feature.mypage.appinfo.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface AppInfoIntent : NeveraIntent {
    data object NavigateBack : AppInfoIntent
    data object TermsClicked : AppInfoIntent
    data object PrivacyPolicyClicked : AppInfoIntent
}
