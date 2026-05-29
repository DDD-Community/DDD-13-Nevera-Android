package com.anddd.nevera.feature.notification.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.AppNotificationType
import com.anddd.nevera.domain.usecase.notification.GetNotificationsUseCase
import com.anddd.nevera.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.anddd.nevera.feature.notification.main.model.NotificationIntent
import com.anddd.nevera.feature.notification.main.model.NotificationItemUiModel
import com.anddd.nevera.feature.notification.main.model.NotificationMutation
import com.anddd.nevera.feature.notification.main.model.NotificationSideEffect
import com.anddd.nevera.feature.notification.main.model.NotificationType
import com.anddd.nevera.feature.notification.main.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
) : NeveraViewModel<NotificationUiState, NotificationSideEffect, NotificationIntent, NotificationMutation>(
    NotificationUiState()
) {

    init {
        loadNotifications()
    }

    override fun handleIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.BackClicked -> intent { postSideEffect(NotificationSideEffect.NavigateBack) }
            is NotificationIntent.PermissionChecked -> updatePermission(intent.isGranted)
            is NotificationIntent.NotificationItemClicked -> onNotificationClicked(intent.item)
            NotificationIntent.EnableNotificationClicked -> onEnableNotificationClicked()
        }
    }

    private fun loadNotifications() = intent {
        applyMutation(NotificationMutation.Loading)
        getNotificationsUseCase()
            .onSuccess { notifications ->
                applyMutation(NotificationMutation.NotificationsLoaded(notifications.map { it.toUiModel() }))
            }
            .onFailure {
                applyMutation(NotificationMutation.LoadComplete)
            }
    }

    private fun updatePermission(isGranted: Boolean) = intent {
        applyMutation(NotificationMutation.PermissionUpdated(isGranted))
    }

    private fun onNotificationClicked(item: NotificationItemUiModel) = intent {
        markNotificationAsReadUseCase(item.id)
        applyMutation(NotificationMutation.NotificationRead(item.id))
        postSideEffect(NotificationSideEffect.NavigateByDeeplink(item.deeplink))
    }

    private fun onEnableNotificationClicked() = intent {
        postSideEffect(NotificationSideEffect.NavigateToNotificationSettings)
    }

    override suspend fun Syntax<NotificationUiState, NotificationSideEffect>.applyMutation(
        mutation: NotificationMutation,
    ) {
        when (mutation) {
            NotificationMutation.Loading -> reduce { state.copy(isLoading = true) }
            NotificationMutation.LoadComplete -> reduce { state.copy(isLoading = false) }
            is NotificationMutation.PermissionUpdated -> reduce {
                state.copy(hasNotificationPermission = mutation.isGranted)
            }
            is NotificationMutation.NotificationsLoaded -> reduce {
                state.copy(notifications = mutation.items, isLoading = false)
            }
            is NotificationMutation.NotificationRead -> reduce {
                state.copy(
                    notifications = state.notifications.map { n ->
                        if (n.id == mutation.id) n.copy(isRead = true) else n
                    }
                )
            }
        }
    }
}

private fun AppNotification.toUiModel(): NotificationItemUiModel {
    return NotificationItemUiModel(
        id = id,
        type = when (type) {
            AppNotificationType.EXPIRY_DATE -> NotificationType.EXPIRY_DATE
        },
        title = title,
        subtitle = subtitle,
        receivedAt = receivedAt,
        isRead = isRead,
        deeplink = deeplink,
    )
}
