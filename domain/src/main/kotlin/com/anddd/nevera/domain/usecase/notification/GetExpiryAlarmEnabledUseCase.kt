package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.domain.repository.NotificationRepository
import javax.inject.Inject

class GetExpiryAlarmEnabledUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(): Boolean = notificationRepository.getExpiryAlarmEnabled()
}
