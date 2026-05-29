package com.anddd.nevera.domain.model.notification

data class AppNotification(
    val id: String,
    val type: AppNotificationType,
    val title: String,
    val subtitle: String?,
    val createdAt: Long,
    val isRead: Boolean,
    val deeplink: String,
)

enum class AppNotificationType {
    DEFAULT,
}
