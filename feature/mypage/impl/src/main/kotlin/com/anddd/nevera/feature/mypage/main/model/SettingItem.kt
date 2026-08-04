package com.anddd.nevera.feature.mypage.main.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anddd.nevera.feature.mypage.R
import com.anddd.nevera.core.designsystem.R as DesignSystemR

enum class SettingItem { Notification, Account, AppInfo }

@DrawableRes
fun SettingItem.iconRes(): Int = when (this) {
    SettingItem.Notification -> DesignSystemR.drawable.ic_bell
    SettingItem.Account -> DesignSystemR.drawable.ic_user_circle
    SettingItem.AppInfo -> DesignSystemR.drawable.ic_info
}

@StringRes
fun SettingItem.labelRes(): Int = when (this) {
    SettingItem.Notification -> R.string.setting_notification
    SettingItem.Account -> R.string.setting_account
    SettingItem.AppInfo -> R.string.setting_app_info
}
