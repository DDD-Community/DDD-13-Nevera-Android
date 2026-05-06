package com.anddd.nevera.feature.mypage.main.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class SettingItemType { Notification, Account, AppInfo }

data class SettingItemUiModel(
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    val type: SettingItemType,
)
