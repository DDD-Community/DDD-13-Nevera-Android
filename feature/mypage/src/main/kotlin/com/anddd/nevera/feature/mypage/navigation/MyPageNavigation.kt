package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.notification.api.NotificationRoute
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen
import com.anddd.nevera.feature.mypage.main.MyPageScreen
import com.anddd.nevera.feature.mypage.settingaccount.SettingAccountScreen
import com.anddd.nevera.feature.mypage.settingnotification.SettingNotificationScreen
import kotlinx.serialization.Serializable

@Serializable
data object MyPageGraphRoute
@Serializable
data object MyPageRoute

@Serializable
private data object AppInfoRoute

@Serializable
private data object SettingAccountRoute

@Serializable
private data object SettingNotificationRoute

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
