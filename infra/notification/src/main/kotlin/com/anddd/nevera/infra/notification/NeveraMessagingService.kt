package com.anddd.nevera.infra.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.anddd.nevera.domain.model.notification.AppNotificationType
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.infra.notification.worker.NotificationSaveWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class NeveraMessagingService : FirebaseMessagingService() {

    @Inject lateinit var fcmSyncScheduler: FcmSyncScheduler

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcmSyncScheduler.scheduleUpdateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val id = remoteMessage.data[NOTIFICATION_ID]
        val type = NotificationType.from(remoteMessage.data[NOTIFICATION_TYPE])
        val title = remoteMessage.data[NOTIFICATION_TITLE]
        val message = remoteMessage.data[NOTIFICATION_MESSAGE]
        val deepLink = remoteMessage.data[NOTIFICATION_DEEPLINK] ?: DEFAULT_DEEP_LINK
        val createdAt = remoteMessage.data[NOTIFICATION_CREATED_AT]

        when (type) {
            NotificationType.DEFAULT -> handleDefaultNotification(
                id = id,
                title = title,
                message = message,
                createdAt = createdAt,
                deepLink = deepLink,
            )
            NotificationType.UNKNOWN -> handleUnknownNotification(remoteMessage.data[NOTIFICATION_TYPE])
        }
    }

    private fun handleDefaultNotification(
        id: String?,
        title: String?,
        message: String?,
        createdAt: String?,
        deepLink: String,
    ) {
        if (id == null) {
            Timber.d("페이로드에 id가 미포함되어 알림을 건너뜁니다")
            return
        }

        enqueueNotificationSave(
            id = id,
            type = AppNotificationType.DEFAULT,
            title = title.orEmpty(),
            subtitle = message,
            createdAt = createdAt.toEpochMilliOrNow(),
            deeplink = deepLink,
        )

        showNotification(
            title = title,
            body = message,
            notificationId = id.toNotificationId(NotificationType.DEFAULT),
            deepLink = deepLink,
        )
    }

    private fun handleUnknownNotification(type: String?) {
        Timber.e("알 수 없는 알림 타입 수신, type: $type")
    }

    private fun showNotification(
        title: String?,
        body: String?,
        notificationId: Int,
        deepLink: String,
    ) {
        if (!canNotify()) {
            Timber.d("알림 권한 미승인 또는 알림 비활성화 상태로 notify를 건너뜁니다.")
            return
        }

        val pendingIntent = createPendingIntent(notificationId, deepLink)
        val notification = buildNotification(title, body, pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createPendingIntent(
        requestCode: Int,
        deepLink: String,
    ): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, deepLink.toUri()).apply {
            // FCM 서비스(Activity 컨텍스트가 아님)에서 액티비티를 시작하므로 NEW_TASK가 필요하다.
            // SINGLE_TOP을 함께 지정해 앱 실행 중에는 MainActivity가 재생성되지 않고 onNewIntent로 재사용되게 한다.
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun buildNotification(
        title: String?,
        body: String?,
        pendingIntent: PendingIntent,
    ): Notification {
        val builder = NotificationCompat.Builder(
            this,
            getString(R.string.default_notification_channel_id)
        )

        return builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun String.toNotificationId(type: NotificationType): Int =
        toIntOrNull() ?: hashCode()

    private fun enqueueNotificationSave(
        id: String,
        type: AppNotificationType,
        title: String,
        subtitle: String?,
        createdAt: Long,
        deeplink: String,
    ) {
        WorkManager.getInstance(this).enqueueUniqueWork(
            NotificationSaveWorker.uniqueWorkName(id),
            ExistingWorkPolicy.KEEP,
            NotificationSaveWorker.createRequest(
                id = id,
                type = type,
                title = title,
                subtitle = subtitle,
                createdAt = createdAt,
                deeplink = deeplink,
            ),
        )
    }

    private fun String?.toEpochMilliOrNow(): Long {
        if (this == null) {
            Timber.w("FCM createdAt이 누락되어 현재 시각으로 대체합니다.")
            return System.currentTimeMillis()
        }

        return runCatching {
            ZonedDateTime.parse(this).toInstant().toEpochMilli()
        }.getOrElse { exception ->
            Timber.w(exception, "FCM createdAt 파싱에 실패하여 현재 시각으로 대체합니다. createdAt=$this")
            System.currentTimeMillis()
        }
    }

    private fun canNotify(): Boolean {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            return false
        }

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val NOTIFICATION_ID = "id"
        private const val NOTIFICATION_TYPE = "type"
        private const val NOTIFICATION_TITLE = "title"
        private const val NOTIFICATION_MESSAGE = "message"
        private const val NOTIFICATION_DEEPLINK = "deeplink"
        private const val NOTIFICATION_CREATED_AT = "createdAt"
        private const val DEFAULT_DEEP_LINK = "nevera://"
    }
}
