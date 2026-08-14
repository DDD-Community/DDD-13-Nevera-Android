package com.anddd.nevera.feature.notification.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface NotificationIntent : NeveraIntent {
    data object BackClicked : NotificationIntent
    data class PermissionChecked(val isGranted: Boolean) : NotificationIntent
    data class NotificationItemClicked(val id: String, val deeplink: String) : NotificationIntent
    data object EnableNotificationClicked : NotificationIntent
}
