package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.mypage.api.AppInfoRoute
import com.anddd.nevera.feature.mypage.api.MyPageRoute
import com.anddd.nevera.feature.mypage.api.SettingAccountRoute
import com.anddd.nevera.feature.mypage.api.SettingNotificationRoute
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen
import com.anddd.nevera.feature.mypage.main.MyPageScreen
import com.anddd.nevera.feature.mypage.settingaccount.SettingAccountScreen
import com.anddd.nevera.feature.mypage.settingnotification.SettingNotificationScreen
import com.anddd.nevera.feature.notification.api.NotificationRoute

/**
 * 목적지를 평평하게 등록한다. "어느 스택에 속하는가"는 이동한 시점의
 * NavigationState가 결정하므로 여기서 묶어 둘 것이 없다.
 */
fun EntryProviderScope<NavKey>.myPageEntry(
    navigator: Navigator,
    onSignedOut: () -> Unit,
) {
    entry<MyPageRoute> {
        MyPageScreen(
            onNavigateToAppInfo = { navigator.navigate(AppInfoRoute) },
            onNavigateToAccountSetting = { navigator.navigate(SettingAccountRoute) },
            onNavigateToNotificationSetting = { navigator.navigate(SettingNotificationRoute) },
            onNavigateToNotification = { navigator.navigate(NotificationRoute) },
        )
    }
    entry<AppInfoRoute> {
        AppInfoScreen(onNavigateBack = navigator::goBack)
    }
    entry<SettingAccountRoute> {
        SettingAccountScreen(
            onNavigateBack = navigator::goBack,
            onNavigateToLogin = onSignedOut,
        )
    }
    entry<SettingNotificationRoute> {
        SettingNotificationScreen(onNavigateBack = navigator::goBack)
    }
}
