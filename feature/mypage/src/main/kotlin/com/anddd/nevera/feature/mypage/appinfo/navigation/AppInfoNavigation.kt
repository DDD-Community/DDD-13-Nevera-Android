package com.anddd.nevera.feature.mypage.appinfo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen

const val APP_INFO_ROUTE = "app_info"

fun NavGraphBuilder.appInfoScreen(
    onNavigateBack: () -> Unit,
) {
    composable(route = APP_INFO_ROUTE) {
        AppInfoScreen(onNavigateBack = onNavigateBack)
    }
}
