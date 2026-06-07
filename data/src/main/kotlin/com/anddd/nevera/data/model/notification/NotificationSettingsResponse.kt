package com.anddd.nevera.data.model.notification

import com.google.gson.annotations.SerializedName

internal data class NotificationSettingsResponse(
    @SerializedName("notificationEnabled") val notificationEnabled: Boolean,
    @SerializedName("notificationHour") val notificationHour: Int,
    @SerializedName("notificationMinute") val notificationMinute: Int,
)
