package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUnreadNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    operator fun invoke(): Flow<Boolean> = notificationRepository.hasUnread()
}
