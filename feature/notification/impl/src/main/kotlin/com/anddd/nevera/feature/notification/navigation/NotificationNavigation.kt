package com.anddd.nevera.feature.notification.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.notification.api.NotificationRoute
import com.anddd.nevera.feature.notification.main.NotificationScreen

fun NavGraphBuilder.notificationScreen(
    onBack: () -> Unit,
    onDeeplink: (deeplink: String) -> Unit = {},
) {
    composable<NotificationRoute> {
        NotificationScreen(
            onBack = onBack,
            onDeeplink = onDeeplink,
        )
    }
}
