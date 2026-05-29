package com.anddd.nevera.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["receivedAt"])],
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val subtitle: String?,
    val receivedAt: Long,
    val isRead: Boolean = false,
    val deeplink: String,
)
