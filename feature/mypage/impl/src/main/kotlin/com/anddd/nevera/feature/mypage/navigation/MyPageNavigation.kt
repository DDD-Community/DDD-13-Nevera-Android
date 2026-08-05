package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.notification.api.NotificationRoute
import com.anddd.nevera.feature.mypage.api.AppInfoRoute
import com.anddd.nevera.feature.mypage.api.MyPageGraphRoute
import com.anddd.nevera.feature.mypage.api.MyPageRoute
import com.anddd.nevera.feature.mypage.api.SettingAccountRoute
import com.anddd.nevera.feature.mypage.api.SettingNotificationRoute
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen
import com.anddd.nevera.feature.mypage.main.MyPageScreen
import com.anddd.nevera.feature.mypage.settingaccount.SettingAccountScreen
import com.anddd.nevera.feature.mypage.settingnotification.SettingNotificationScreen

fun NavGraphBuilder.myPageNavGraph(
    navigator: Navigator,
    onNavigateToLogin: () -> Unit,
) {
    navigation<MyPageGraphRoute>(startDestination = MyPageRoute) {
        composable<MyPageRoute> {
            MyPageScreen(
                onNavigateToAppInfo = { navigator.navigate(AppInfoRoute) },
                onNavigateToAccountSetting = { navigator.navigate(SettingAccountRoute) },
                onNavigateToNotificationSetting = { navigator.navigate(SettingNotificationRoute) },
                onNavigateToNotification = { navigator.navigate(NotificationRoute) },
            )
        }
        composable<AppInfoRoute> {
            AppInfoScreen(
                onNavigateBack = { navigator.goBack() },
            )
        }
        composable<SettingAccountRoute> {
            SettingAccountScreen(
                onNavigateBack = { navigator.goBack() },
                onNavigateToLogin = onNavigateToLogin,
            )
        }
        composable<SettingNotificationRoute> {
            SettingNotificationScreen(
                onNavigateBack = { navigator.goBack() },
            )
        }
    }
}
