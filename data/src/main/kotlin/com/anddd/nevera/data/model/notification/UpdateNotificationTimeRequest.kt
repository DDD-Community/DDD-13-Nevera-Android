package com.anddd.nevera.data.model.notification

internal data class UpdateNotificationTimeRequest(
    val notificationHour: Int,
    val notificationMinute: Int,
)
