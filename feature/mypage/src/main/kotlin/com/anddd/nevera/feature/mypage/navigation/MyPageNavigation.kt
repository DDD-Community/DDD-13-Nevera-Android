package com.anddd.nevera.feature.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen
import com.anddd.nevera.feature.mypage.main.MyPageScreen
import com.anddd.nevera.feature.mypage.settingaccount.SettingAccountScreen
import kotlinx.serialization.Serializable

@Serializable
data object MyPageGraphRoute
@Serializable
data object MyPageRoute

@Serializable
private data object AppInfoRoute

@Serializable
private data object SettingAccountRoute

fun NavGraphBuilder.myPageNavGraph(
    navController: NavController,
    onNavigateToLogin: () -> Unit,
) {
    navigation<MyPageGraphRoute>(startDestination = MyPageRoute) {
        composable<MyPageRoute> {
            MyPageScreen(
                onNavigateToAppInfo = { navController.navigate(AppInfoRoute) },
                onNavigateToAccountSetting = { navController.navigate(SettingAccountRoute) },
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
    }
}
