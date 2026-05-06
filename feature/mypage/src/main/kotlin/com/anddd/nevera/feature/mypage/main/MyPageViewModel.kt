package com.anddd.nevera.feature.mypage.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.designsystem.R as DesignSystemR
import com.anddd.nevera.feature.mypage.R
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageSideEffect
import com.anddd.nevera.feature.mypage.main.model.MyPageStatus
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState
import com.anddd.nevera.feature.mypage.main.model.SettingItemUiModel
import com.anddd.nevera.feature.mypage.main.model.SettingItemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyPageUiState(
            settingItems = listOf(
                SettingItemUiModel(
                    iconRes = DesignSystemR.drawable.ic_bell,
                    labelRes = R.string.setting_notification,
                    type = SettingItemType.Notification,
                ),
                SettingItemUiModel(
                    iconRes = DesignSystemR.drawable.ic_user_circle,
                    labelRes = R.string.setting_account,
                    type = SettingItemType.Account,
                ),
                SettingItemUiModel(
                    iconRes = DesignSystemR.drawable.ic_info,
                    labelRes = R.string.setting_app_info,
                    type = SettingItemType.AppInfo,
                )
            ),
        )
    )
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<MyPageSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    // ① 단일 진입점 — View → ViewModel 유일한 경로 (UDF 강제)
    fun processIntent(intent: MyPageIntent) {
        _uiState.update { reduce(it, intent) }
        handleEffect(intent)
    }

    // ② 순수 함수 — 동기적 상태 전이 (부수 효과 없음, 테스트 용이)
    private fun reduce(state: MyPageUiState, intent: MyPageIntent): MyPageUiState = when (intent) {
        MyPageIntent.Load -> state.copy(status = MyPageStatus.Loading)
        is MyPageIntent.SettingItemClicked -> state
    }

    // ③ 비동기 부수 효과 분기 — 새 비동기 Intent 추가 시 여기와 private 함수만 수정
    private fun handleEffect(intent: MyPageIntent) {
        when (intent) {
            MyPageIntent.Load -> load()
            is MyPageIntent.SettingItemClicked -> when (intent.type) {
                SettingItemType.Notification -> {}
                SettingItemType.Account -> {}
                SettingItemType.AppInfo -> {}
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            // TODO: UseCase로 초기 데이터 로드
            _uiState.update { it.copy(status = MyPageStatus.Idle) }
        }
    }
}
