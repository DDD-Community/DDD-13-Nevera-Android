package com.anddd.nevera.feature.mypage.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.mypage.main.MyPageScreen
import kotlinx.serialization.Serializable

@Serializable
data object MyPageRoute

fun NavGraphBuilder.myPageScreen(
    onNavigateToAppInfo: () -> Unit,
    onNavigateToAccountSetting: () -> Unit,
) {
    composable<MyPageRoute> {
        MyPageScreen(
            onNavigateToAppInfo = onNavigateToAppInfo,
            onNavigateToAccountSetting = onNavigateToAccountSetting,
        )
    }
}
