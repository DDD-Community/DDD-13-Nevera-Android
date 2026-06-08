package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.NotificationTime
import com.anddd.nevera.domain.model.notification.UpdateNotificationTimeError
import com.anddd.nevera.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationTimeUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(hour: Int, minute: Int): NeveraResult<NotificationTime, UpdateNotificationTimeError> =
        notificationRepository.updateNotificationTime(hour, minute)
}
