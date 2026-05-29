package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.notification.NotificationListResponse
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.AppNotificationType
import java.time.ZonedDateTime

internal fun NotificationListResponse.toDomain(): AppNotification = AppNotification(
    id = id.toString(),
    type = AppNotificationType.EXPIRY_DATE,
    title = message,
    subtitle = null,
    receivedAt = ZonedDateTime.parse(createdAt).toInstant().toEpochMilli(),
    isRead = false,
    deeplink = deeplink,
)
