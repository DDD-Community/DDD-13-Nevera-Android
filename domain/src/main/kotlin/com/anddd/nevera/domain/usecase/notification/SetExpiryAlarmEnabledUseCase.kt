package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.domain.repository.NotificationRepository
import javax.inject.Inject

class SetExpiryAlarmEnabledUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = notificationRepository.setExpiryAlarmEnabled(enabled)
}
