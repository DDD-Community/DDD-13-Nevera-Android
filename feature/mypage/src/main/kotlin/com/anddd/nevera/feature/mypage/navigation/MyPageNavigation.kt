package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
    navController: NavController,
    onNavigateToLogin: () -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    navigation<MyPageGraphRoute>(startDestination = MyPageRoute) {
        composable<MyPageRoute> {
            MyPageScreen(
                onNavigateToAppInfo = { navController.navigate(AppInfoRoute) },
                onNavigateToAccountSetting = { navController.navigate(SettingAccountRoute) },
                onNavigateToNotificationSetting = { navController.navigate(SettingNotificationRoute) },
                onNavigateToNotification = onNavigateToNotification,
            )
        }
        composable<AppInfoRoute> {
            AppInfoScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<SettingAccountRoute> {
            SettingAccountScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = onNavigateToLogin,
            )
        }
        composable<SettingNotificationRoute> {
            SettingNotificationScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
