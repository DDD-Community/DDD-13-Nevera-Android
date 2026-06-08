package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.NotificationSettings
import com.anddd.nevera.domain.model.notification.UpdateNotificationEnabledError
import com.anddd.nevera.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationEnabledUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(
        enabled: Boolean
    ): NeveraResult<NotificationSettings, UpdateNotificationEnabledError> {
        return notificationRepository.updateNotificationEnabled(enabled)
    }
}
