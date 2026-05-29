package com.anddd.nevera.data.mapper

import com.anddd.nevera.core.database.entity.NotificationEntity
import com.anddd.nevera.data.model.notification.NotificationListResponse
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.AppNotificationType
import java.time.ZonedDateTime

internal fun NotificationListResponse.toDomain(): AppNotification = AppNotification(
    id = id.toString(),
    type = type.toAppNotificationType(),
    title = message,
    subtitle = null,
    createdAt = runCatching {
        ZonedDateTime.parse(createdAt).toInstant().toEpochMilli()
    }.getOrElse { System.currentTimeMillis() },
    isRead = false,
    deeplink = deeplink,
)

internal fun NotificationListResponse.toEntity(): NotificationEntity = NotificationEntity(
    id = id.toString(),
    type = type,
    title = message,
    subtitle = null,
    createdAt = runCatching {
        ZonedDateTime.parse(createdAt).toInstant().toEpochMilli()
    }.getOrElse { System.currentTimeMillis() },
    isRead = false,
    deeplink = deeplink,
)

internal fun NotificationEntity.toDomain(): AppNotification = AppNotification(
    id = id,
    type = type.toAppNotificationType(),
    title = title,
    subtitle = subtitle,
    createdAt = createdAt,
    isRead = isRead,
    deeplink = deeplink,
)

internal fun AppNotification.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    type = type.name,
    title = title,
    subtitle = subtitle,
    createdAt = createdAt,
    isRead = isRead,
    deeplink = deeplink,
)

private fun String.toAppNotificationType(): AppNotificationType = when (this) {
    "DEFAULT", "default" -> AppNotificationType.DEFAULT
    else -> AppNotificationType.DEFAULT // TODO: 서버 타입 추가 시 분기 확장
}
