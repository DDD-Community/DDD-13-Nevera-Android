package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraDisplayAppBar
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState
import com.anddd.nevera.feature.mypage.main.model.ProfileUiModel
import com.anddd.nevera.feature.mypage.main.model.SettingItem
import com.anddd.nevera.feature.mypage.R as MyPageR

@Composable
internal fun MyPageContent(
    uiState: MyPageUiState,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NeveraTheme.colors.backgroundPrimary,
        topBar = {
            NeveraDisplayAppBar(
                title = stringResource(MyPageR.string.mypage_title),
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = if (uiState.hasUnreadNotification) NeveraIcons.BellOn else NeveraIcons.Bell,
                        contentDescription = stringResource(MyPageR.string.mypage_notification_icon_desc),
                        onClick = { onIntent(MyPageIntent.NotificationIconClicked) },
                    )
                ),
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column {
                ProfileContent(profile = uiState.profile)

                Box(
                    modifier = Modifier
                        .background(NeveraTheme.colors.dividerNormal)
                        .fillMaxWidth()
                        .height(NeveraTheme.spacing.gap8)
                )

                SettingsContent(
                    settingItems = uiState.settingItems,
                    onClick = { type -> onIntent(MyPageIntent.SettingItemClicked(type)) }
                )
            }

            if (uiState.isLoading) {
                LoadingContent()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MyPageContentPreview() {
    NeveraTheme {
        MyPageContent(
            uiState = MyPageUiState(
                profile = ProfileUiModel("hong@example.com"),
                settingItems = listOf(
                    SettingItem.Notification,
                    SettingItem.Account,
                    SettingItem.AppInfo,
                ),
            ),
            onIntent = {},
        )
    }
}
