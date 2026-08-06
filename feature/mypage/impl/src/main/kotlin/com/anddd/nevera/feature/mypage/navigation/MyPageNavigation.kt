package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.nav3.Nav3Navigator
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
 * Navigation 3에는 중첩 그래프가 없다. 목적지를 평평하게 등록하고,
 * "어느 탭에 속하는가"는 NavigationState의 서브스택이 결정한다.
 */
fun EntryProviderScope<NavKey>.myPageEntry(
    navigator: Nav3Navigator,
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
