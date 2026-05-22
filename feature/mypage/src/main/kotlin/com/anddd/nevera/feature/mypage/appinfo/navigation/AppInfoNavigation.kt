package com.anddd.nevera.feature.mypage.appinfo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.mypage.appinfo.AppInfoScreen
import kotlinx.serialization.Serializable

@Serializable
data object AppInfoRoute

fun NavGraphBuilder.appInfoScreen(
    onNavigateBack: () -> Unit,
) {
    composable<AppInfoRoute> {
        AppInfoScreen(onNavigateBack = onNavigateBack)
    }
}
