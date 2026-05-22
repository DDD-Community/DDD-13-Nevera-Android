package com.anddd.nevera.feature.mypage.settingaccount.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.mypage.settingaccount.SettingAccountScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingAccountRoute

fun NavGraphBuilder.settingAccountScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<SettingAccountRoute> {
        SettingAccountScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}
