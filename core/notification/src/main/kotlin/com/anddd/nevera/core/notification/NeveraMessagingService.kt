package com.anddd.nevera.core.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.core.content.ContextCompat
import com.anddd.nevera.domain.model.notification.logFcmSyncFailure
import com.anddd.nevera.domain.usecase.notification.UpdateFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class NeveraMessagingService : FirebaseMessagingService() {

    @Inject lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            try {
                updateFcmTokenUseCase(token)
                    .logFcmSyncFailure(TAG, BuildConfig.DEBUG, Log::w)
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "FCM 토큰 업데이트 실패", t)
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var type = NotificationType.from(remoteMessage.data[NOTIFICATION_TYPE])
        val title = remoteMessage.data[NOTIFICATION_TITLE]
        val body = remoteMessage.data[NOTIFICATION_BODY]
        val deepLink = remoteMessage.data[NOTIFICATION_DEEPLINK] ?: DEFAULT_DEEP_LINK

        // TODO :: 현재 type과 deepLink가 전달되고 있지 않은 상황, 임시로 type을 default로 설정합니다.
        type = NotificationType.DEFAULT

        when (type) {
            NotificationType.DEFAULT -> {
                showNotification(
                    title = title,
                    body = body,
                    type = type,
                    deepLink = deepLink,
                )
            }
            NotificationType.UNKNOWN -> {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "unknown type, $remoteMessage")
                }
            }
        }
    }

    private fun showNotification(
        title: String?,
        body: String?,
        type: NotificationType,
        deepLink: String,
    ) {
        val intent = Intent(Intent.ACTION_VIEW, deepLink.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            type.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notificationBuilder = NotificationCompat.Builder(
            this,
            getString(R.string.default_notification_channel_id)
        )

        val notification = notificationBuilder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(applicationInfo.icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (!canNotify()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "알림 권한 미승인 또는 알림 비활성화 상태로 notify를 건너뜁니다.")
            }
            return
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(type.ordinal, notification)
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

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "NeveraMessagingService"
        private const val NOTIFICATION_TYPE = "type"
        private const val NOTIFICATION_TITLE = "title"
        private const val NOTIFICATION_BODY = "body"
        private const val NOTIFICATION_DEEPLINK = "deepLink"
        private const val DEFAULT_DEEP_LINK = "nevera://"
    }
}
