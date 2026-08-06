package com.anddd.nevera.feature.notification.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.nav3.Nav3Navigator
import com.anddd.nevera.feature.notification.api.NotificationRoute
import com.anddd.nevera.feature.notification.main.NotificationScreen

fun EntryProviderScope<NavKey>.notificationEntry(
    navigator: Nav3Navigator,
    onDeeplink: (String) -> Unit,
) {
    entry<NotificationRoute> {
        NotificationScreen(
            onBack = navigator::goBack,
            onDeeplink = onDeeplink,
        )
    }
}
