package com.anddd.nevera.infra.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.AppNotificationType
import com.anddd.nevera.domain.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class NotificationSaveWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository,
) : CoroutineWorker(context, workerParams) {

    private val isMaxRetriesExceeded: Boolean
        get() = runAttemptCount >= MAX_RETRIES

    override suspend fun doWork(): Result {
        val notification = inputData.toNotification() ?: return Result.failure()

        return try {
            notificationRepository.insert(notification)
            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Timber.e(exception, "알림 저장에 실패했습니다. id=${notification.id}")
            if (isMaxRetriesExceeded) Result.failure() else Result.retry()
        }
    }

    private fun Data.toNotification(): AppNotification? {
        val id = getRequiredString(KEY_ID) ?: return null
        val type = getRequiredString(KEY_TYPE)?.toAppNotificationType() ?: return null
        val title = getRequiredString(KEY_TITLE) ?: return null
        val createdAt = getRequiredLong(KEY_CREATED_AT) ?: return null
        val deeplink = getRequiredString(KEY_DEEPLINK) ?: return null

        return AppNotification(
            id = id,
            type = type,
            title = title,
            subtitle = getString(KEY_SUBTITLE),
            createdAt = createdAt,
            isRead = false,
            deeplink = deeplink,
        )
    }

    private fun Data.getRequiredString(key: String): String? {
        val value = getString(key)
        if (value == null) {
            Timber.e("알림 저장 Worker input이 누락되었습니다. key=$key")
        }
        return value
    }

    private fun Data.getRequiredLong(key: String): Long? {
        val value = keyValueMap[key] as? Long
        if (value == null) {
            Timber.e("알림 저장 Worker input이 누락되었습니다. key=$key")
        }
        return value
    }

    private fun String.toAppNotificationType(): AppNotificationType? =
        runCatching { AppNotificationType.valueOf(this) }.getOrElse { exception ->
            Timber.e(exception, "알림 저장 Worker input type이 올바르지 않습니다. type=$this")
            null
        }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TYPE = "type"
        private const val KEY_TITLE = "title"
        private const val KEY_SUBTITLE = "subtitle"
        private const val KEY_CREATED_AT = "created_at"
        private const val KEY_DEEPLINK = "deeplink"
        private const val MAX_RETRIES = 5

        fun uniqueWorkName(id: String): String = "notification_save_$id"

        internal fun createInputData(
            id: String,
            type: AppNotificationType,
            title: String,
            subtitle: String?,
            createdAt: Long,
            deeplink: String,
        ): Data =
            Data.Builder()
                .putString(KEY_ID, id)
                .putString(KEY_TYPE, type.name)
                .putString(KEY_TITLE, title)
                .putLong(KEY_CREATED_AT, createdAt)
                .putString(KEY_DEEPLINK, deeplink)
                .apply {
                    if (subtitle != null) {
                        putString(KEY_SUBTITLE, subtitle)
                    }
                }.build()

        fun createRequest(
            id: String,
            type: AppNotificationType,
            title: String,
            subtitle: String?,
            createdAt: Long,
            deeplink: String,
        ): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotificationSaveWorker>()
                .setInputData(
                    createInputData(
                        id = id,
                        type = type,
                        title = title,
                        subtitle = subtitle,
                        createdAt = createdAt,
                        deeplink = deeplink,
                    )
                )
                .build()
        }
    }
}
