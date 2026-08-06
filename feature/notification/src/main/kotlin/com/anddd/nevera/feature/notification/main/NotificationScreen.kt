package com.anddd.nevera.feature.notification.main

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import com.anddd.nevera.feature.notification.main.component.NotificationContent
import com.anddd.nevera.feature.notification.main.model.NotificationIntent
import com.anddd.nevera.feature.notification.main.model.NotificationSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onDeeplink: (deeplink: String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.collectAsState().value
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGranted = NotificationManagerCompat.from(context).areNotificationsEnabled()
                viewModel.handleIntent(NotificationIntent.PermissionChecked(isGranted))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            NotificationSideEffect.NavigateBack -> onBack()
            NotificationSideEffect.NavigateToNotificationSettings -> {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
            is NotificationSideEffect.NavigateByDeeplink -> onDeeplink(effect.deeplink)
        }
    }

    NotificationContent(
        uiState = uiState,
        pagingItems = pagingItems,
        onIntent = viewModel::handleIntent,
    )
}
