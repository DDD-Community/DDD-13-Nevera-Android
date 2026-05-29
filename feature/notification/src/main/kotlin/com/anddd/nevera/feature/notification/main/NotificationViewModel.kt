package com.anddd.nevera.feature.notification.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.model.notification.AppNotificationType
import com.anddd.nevera.domain.usecase.notification.GetNotificationsUseCase
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.anddd.nevera.feature.notification.main.model.NotificationIntent
import com.anddd.nevera.feature.notification.main.model.NotificationItemUiModel
import com.anddd.nevera.feature.notification.main.model.NotificationMutation
import com.anddd.nevera.feature.notification.main.model.NotificationSideEffect
import com.anddd.nevera.feature.notification.main.model.NotificationType
import com.anddd.nevera.feature.notification.main.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
) : NeveraViewModel<NotificationUiState, NotificationSideEffect, NotificationIntent, NotificationMutation>(
    NotificationUiState()
) {
    val pagingFlow: Flow<PagingData<NotificationItemUiModel>> =
        getNotificationsUseCase()
            .map { pagingData -> pagingData.map { it.toUiModel() } }
            .cachedIn(viewModelScope)

    init {
        markAllAsRead()
    }

    override fun handleIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.BackClicked -> intent { postSideEffect(NotificationSideEffect.NavigateBack) }
            is NotificationIntent.PermissionChecked -> updatePermission(intent.isGranted)
            is NotificationIntent.NotificationItemClicked -> onNotificationClicked(intent.id, intent.deeplink)
            NotificationIntent.EnableNotificationClicked -> onEnableNotificationClicked()
        }
    }

    private fun markAllAsRead() = intent {
        markAllNotificationsAsReadUseCase()
    }

    private fun updatePermission(isGranted: Boolean) = intent {
        applyMutation(NotificationMutation.PermissionUpdated(isGranted))
    }

    private fun onNotificationClicked(id: String, deeplink: String) = intent {
        markNotificationAsReadUseCase(id)
        postSideEffect(NotificationSideEffect.NavigateByDeeplink(deeplink))
    }

    private fun onEnableNotificationClicked() = intent {
        postSideEffect(NotificationSideEffect.NavigateToNotificationSettings)
    }

    override suspend fun Syntax<NotificationUiState, NotificationSideEffect>.applyMutation(
        mutation: NotificationMutation,
    ) {
        when (mutation) {
            is NotificationMutation.PermissionUpdated -> reduce {
                state.copy(hasNotificationPermission = mutation.isGranted)
            }
        }
    }
}

private fun AppNotification.toUiModel(): NotificationItemUiModel =
    NotificationItemUiModel(
        id = id,
        type = when (type) {
            AppNotificationType.DEFAULT -> NotificationType.DEFAULT
        },
        title = title,
        subtitle = subtitle,
        createdAt = createdAt,
        isRead = isRead,
        deeplink = deeplink,
    )
