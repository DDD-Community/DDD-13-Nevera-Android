package com.anddd.nevera.feature.notification.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface NotificationMutation : NeveraMutation {
    data class PermissionUpdated(val isGranted: Boolean) : NotificationMutation
}
