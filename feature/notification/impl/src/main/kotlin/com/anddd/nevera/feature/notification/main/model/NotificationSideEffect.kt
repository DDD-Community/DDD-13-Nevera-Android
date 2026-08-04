package com.anddd.nevera.feature.notification.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface NotificationSideEffect : NeveraSideEffect {
    data object NavigateBack : NotificationSideEffect
    data object NavigateToNotificationSettings : NotificationSideEffect
    data class NavigateByDeeplink(val deeplink: String) : NotificationSideEffect
}
