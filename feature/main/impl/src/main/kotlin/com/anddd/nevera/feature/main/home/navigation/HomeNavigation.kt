package com.anddd.nevera.feature.main.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.nav3.Nav3Navigator
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.main.home.HomeScreen
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute
import com.anddd.nevera.feature.notification.api.NotificationRoute

fun EntryProviderScope<NavKey>.homeEntry(navigator: Nav3Navigator) {
    entry<HomeRoute> {
        HomeScreen(
            onNavigateToCamera = { navigator.navigate(OcrCaptureRoute()) },
            onNavigateToGallery = { navigator.navigate(OcrCaptureRoute(openGallery = true)) },
            onNavigateToNotification = { navigator.navigate(NotificationRoute) },
        )
    }
}
