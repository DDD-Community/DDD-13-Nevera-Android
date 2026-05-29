package com.anddd.nevera.data.model.notification

import com.google.gson.annotations.SerializedName

internal data class NotificationListResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("deeplink")
    val deeplink: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("createdAt")
    val createdAt: String,
)
