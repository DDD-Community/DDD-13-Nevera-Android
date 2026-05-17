package com.anddd.nevera.feature.mypage.main

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageMutation
import com.anddd.nevera.feature.mypage.main.model.MyPageSideEffect
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState
import com.anddd.nevera.feature.mypage.main.model.SettingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(

) : NeveraViewModel<MyPageUiState, MyPageSideEffect, MyPageIntent, MyPageMutation>(
    MyPageUiState(
        settingItems = listOf(
            SettingItem.Notification,
            SettingItem.Account,
            SettingItem.AppInfo,
        ),
    ),
) {
    init {
        load()
    }

    override fun handleIntent(intent: MyPageIntent) {
        when (intent) {
            is MyPageIntent.SettingItemClicked -> onSettingItemClicked(intent.item)
        }
    }

    private fun load() = intent {
        applyMutation(MyPageMutation.Loading)
        // TODO: UseCase로 초기 데이터 로드
        applyMutation(MyPageMutation.LoadComplete)
    }

    private fun onSettingItemClicked(item: SettingItem) = intent {
        when (item) {
            SettingItem.Notification -> {}

            SettingItem.Account -> {}

            SettingItem.AppInfo -> postSideEffect(MyPageSideEffect.NavigateToAppInfo)
        }
    }

    override suspend fun Syntax<MyPageUiState, MyPageSideEffect>.applyMutation(mutation: MyPageMutation) {
        when (mutation) {
            MyPageMutation.Loading -> reduce { state.copy(isLoading = true) }
            MyPageMutation.LoadComplete -> reduce { state.copy(isLoading = false) }
        }
    }
}
