package com.anddd.nevera.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannelInitializer {

    fun initialize(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            context.getString(R.string.default_notification_channel_id),
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
    }
}
