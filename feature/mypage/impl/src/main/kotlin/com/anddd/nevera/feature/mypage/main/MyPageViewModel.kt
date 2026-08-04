package com.anddd.nevera.feature.mypage.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.domain.usecase.user.GetUserProfileUseCase
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageMutation
import com.anddd.nevera.feature.mypage.main.model.MyPageSideEffect
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState
import com.anddd.nevera.feature.mypage.main.model.SettingItem
import com.anddd.nevera.feature.mypage.main.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    val getProfile: GetUserProfileUseCase,
    private val observeUnreadNotification: ObserveUnreadNotificationUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase,
) : NeveraViewModel<MyPageUiState, MyPageSideEffect, MyPageIntent, MyPageMutation>(
    MyPageUiState(
        settingItems = persistentListOf(
            SettingItem.Notification,
            SettingItem.Account,
            SettingItem.AppInfo,
        ),
    ),
) {
    init {
        observeBadge()
        load()
    }

    override fun handleIntent(intent: MyPageIntent) {
        when (intent) {
            is MyPageIntent.SettingItemClicked -> onSettingItemClicked(intent.item)
            MyPageIntent.NotificationIconClicked -> onNotificationIconClick()
        }
    }

    private fun observeBadge() = intent {
        observeUnreadNotification().collect { hasUnread ->
            applyMutation(MyPageMutation.BadgeUpdated(hasUnread))
        }
    }

    private fun onNotificationIconClick() = intent {
        markAllNotificationsAsRead()
        postSideEffect(MyPageSideEffect.NavigateToNotification)
    }

    private fun load() = intent {
        applyMutation(MyPageMutation.Loading)
        getProfile()
            .onSuccess { profile ->
                applyMutation(MyPageMutation.ShowProfile(profile.toUiModel()))
                applyMutation(MyPageMutation.LoadComplete)
            }
            .onFailure {
                applyMutation(MyPageMutation.LoadComplete)
                postSideEffect(MyPageSideEffect.ShowNetworkErrorToast)
            }

    }

    private fun onSettingItemClicked(item: SettingItem) = intent {
        when (item) {
            SettingItem.Notification -> postSideEffect(MyPageSideEffect.NavigateToNotificationSetting)

            SettingItem.Account -> postSideEffect(MyPageSideEffect.NavigateToAccountSetting)

            SettingItem.AppInfo -> postSideEffect(MyPageSideEffect.NavigateToAppInfo)
        }
    }

    override suspend fun Syntax<MyPageUiState, MyPageSideEffect>.applyMutation(mutation: MyPageMutation) {
        when (mutation) {
            MyPageMutation.Loading -> reduce { state.copy(isLoading = true) }
            MyPageMutation.LoadComplete -> reduce { state.copy(isLoading = false) }
            is MyPageMutation.ShowProfile -> reduce { state.copy(profile = mutation.profile) }
            is MyPageMutation.BadgeUpdated -> reduce { state.copy(hasUnreadNotification = mutation.hasUnread) }
        }
    }
}
