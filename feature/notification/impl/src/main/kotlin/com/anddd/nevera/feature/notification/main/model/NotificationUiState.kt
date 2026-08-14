package com.anddd.nevera.feature.notification.main.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState

@Immutable
data class NotificationUiState(
    val hasNotificationPermission: Boolean = true,
) : NeveraState

data class NotificationItemUiModel(
    val id: String,
    val type: NotificationType,
    val title: String,
    val subtitle: String? = null,
    val createdAt: Long,
    val isRead: Boolean,
    val deeplink: String,
)

enum class NotificationType {
    DEFAULT,
}
