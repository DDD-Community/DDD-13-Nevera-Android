package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface MyPageIntent : NeveraIntent {
    data class SettingItemClicked(val item: SettingItem) : MyPageIntent
}
