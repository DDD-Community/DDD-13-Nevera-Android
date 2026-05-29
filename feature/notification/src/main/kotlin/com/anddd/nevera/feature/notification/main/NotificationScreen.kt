package com.anddd.nevera.feature.notification.main

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.notification.R
import com.anddd.nevera.feature.notification.main.component.NotificationEmptyState
import com.anddd.nevera.feature.notification.main.component.NotificationItemRow
import com.anddd.nevera.feature.notification.main.component.NotificationPermissionBanner
import com.anddd.nevera.feature.notification.main.model.NotificationIntent
import com.anddd.nevera.feature.notification.main.model.NotificationItemUiModel
import com.anddd.nevera.feature.notification.main.model.NotificationSideEffect
import com.anddd.nevera.feature.notification.main.model.NotificationType
import com.anddd.nevera.feature.notification.main.model.NotificationUiState
import com.anddd.nevera.core.ui.component.LoadingContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.concurrent.TimeUnit

private val NotificationFooterHeight = 84.dp

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onDeeplink: (deeplink: String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.collectAsState().value

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

    Box(modifier = Modifier.fillMaxSize()) {
        NotificationContent(
            uiState = uiState,
            onIntent = viewModel::handleIntent,
        )
        if (uiState.isLoading) {
            LoadingContent()
        }
    }
}

@Composable
private fun NotificationContent(
    uiState: NotificationUiState,
    onIntent: (NotificationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NeveraTheme.colors.backgroundPrimary,
        topBar = {
            NeveraAppBar(
                title = stringResource(R.string.notification_title),
                navigation = NeveraAppBarNavigation.Back(
                    onClick = { onIntent(NotificationIntent.BackClicked) }
                ),
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            if (!uiState.hasNotificationPermission) {
                item {
                    NotificationPermissionBanner(
                        onClickEnable = { onIntent(NotificationIntent.EnableNotificationClicked) },
                        modifier = Modifier.padding(
                            horizontal = NeveraTheme.spacing.padding16,
                            vertical = NeveraTheme.spacing.padding12,
                        ),
                    )
                }
            }

            if (uiState.notifications.isEmpty()) {
                item {
                    NotificationEmptyState(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.notifications,
                    key = { _, item -> item.id },
                ) { index, item ->
                    NotificationItemRow(
                        item = item,
                        onClick = {
                            onIntent(
                                NotificationIntent.NotificationItemClicked(
                                    id = item.id,
                                    deeplink = item.deeplink,
                                )
                            )
                        },
                    )
                    if (index < uiState.notifications.lastIndex) {
                        HorizontalDivider(
                            color = NeveraTheme.colors.dividerNormal,
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(NotificationFooterHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.notification_footer),
                            style = NeveraTheme.typography.captionLarge,
                            color = NeveraTheme.colors.textCaption,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private class NotificationUiStateProvider : PreviewParameterProvider<NotificationUiState> {
    private val sampleItems = listOf(
        NotificationItemUiModel(
            id = "1",
            type = NotificationType.EXPIRY_DATE,
            title = "삼겹살(12,000)이 내일까지예요",
            subtitle = "오늘 저녁은 [제육볶음] 어떠세요?",
            receivedAt = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(59),
            isRead = false,
            deeplink = "nevera://detail/101",
        ),
        NotificationItemUiModel(
            id = "2",
            type = NotificationType.EXPIRY_DATE,
            title = "삼겹살(12,000)이 내일까지예요",
            subtitle = "오늘 저녁은 [제육볶음] 어떠세요?",
            receivedAt = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(23),
            isRead = true,
            deeplink = "nevera://detail/102",
        ),
    )

    override val values = sequenceOf(
        NotificationUiState(notifications = emptyList(), hasNotificationPermission = true),
        NotificationUiState(notifications = emptyList(), hasNotificationPermission = false),
        NotificationUiState(notifications = sampleItems, hasNotificationPermission = true),
        NotificationUiState(notifications = sampleItems, hasNotificationPermission = false),
    )
}

@Preview(name = "빈 상태 - 권한 있음")
@Preview(name = "빈 상태 - 권한 없음")
@Preview(name = "목록 - 권한 있음")
@Preview(name = "목록 - 권한 없음")
@Composable
private fun NotificationContentPreview(
    @PreviewParameter(NotificationUiStateProvider::class) uiState: NotificationUiState,
) {
    NeveraTheme {
        NotificationContent(
            uiState = uiState,
            onIntent = {},
        )
    }
}

@Preview(name = "다크모드", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotificationContentDarkPreview() {
    NeveraTheme {
        NotificationContent(
            uiState = NotificationUiState(
                notifications = listOf(
                    NotificationItemUiModel(
                        id = "1",
                        type = NotificationType.EXPIRY_DATE,
                        title = "삼겹살(12,000)이 내일까지예요",
                        subtitle = "오늘 저녁은 [제육볶음] 어떠세요?",
                        receivedAt = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(59),
                        isRead = false,
                        deeplink = "nevera://detail/101",
                    ),
                ),
                hasNotificationPermission = false,
            ),
            onIntent = {},
        )
    }
}
