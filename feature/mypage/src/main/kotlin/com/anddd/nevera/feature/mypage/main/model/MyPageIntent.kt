package com.anddd.nevera.feature.mypage.main.model

sealed interface MyPageIntent {
    data object Load : MyPageIntent
    data class SettingItemClicked(val item: SettingItem) : MyPageIntent
}
