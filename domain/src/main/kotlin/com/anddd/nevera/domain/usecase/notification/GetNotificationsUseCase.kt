package com.anddd.nevera.domain.usecase.notification

import androidx.paging.PagingData
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    operator fun invoke(): Flow<PagingData<AppNotification>> =
        notificationRepository.getNotifications()
}
