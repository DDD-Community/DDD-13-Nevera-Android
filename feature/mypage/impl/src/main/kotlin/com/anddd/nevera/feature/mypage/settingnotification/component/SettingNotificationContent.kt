package com.anddd.nevera.feature.mypage.settingnotification.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.infra.permission.AppPermission
import com.anddd.nevera.infra.permission.DefaultPermissionChecker
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.toggle.NeveraSwitch
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationIntent
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationUiState
import com.anddd.nevera.feature.mypage.settingnotification.model.formattedAlarmTime
import com.anddd.nevera.feature.mypage.R as MyPageR

private val NOTIFICATION_ROW_HEIGHT = 48.dp

@Composable
internal fun SettingNotificationContent(
    uiState: SettingNotificationUiState,
    onIntent: (SettingNotificationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NeveraTheme.colors.backgroundPrimary,
        topBar = {
            NeveraAppBar(
                title = stringResource(MyPageR.string.setting_notification_title),
                navigation = NeveraAppBarNavigation.Back(
                    onClick = { onIntent(SettingNotificationIntent.NavigateBack) }
                ),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap20))

                NotificationSectionHeader()

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

                NotificationAlarmExampleCard()

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

                ExpiryAlarmRow(
                    checked = uiState.isExpiryAlarmEnabled,
                    onIntent = onIntent,
                )

                AlarmTimeRow(
                    formattedTime = uiState.formattedAlarmTime,
                    isEnabled = uiState.isExpiryAlarmEnabled,
                    onClick = { onIntent(SettingNotificationIntent.AlarmTimeClicked) },
                )
            }
        }
    }
}

@Composable
private fun NotificationSectionHeader() {
    Text(
        text = stringResource(MyPageR.string.setting_notification_section_title),
        style = NeveraTheme.typography.titleLarge,
        color = NeveraTheme.colors.textSecondary,
        modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
    )

    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap2))

    Text(
        text = stringResource(MyPageR.string.setting_notification_section_subtitle),
        style = NeveraTheme.typography.captionLarge,
        color = NeveraTheme.colors.textQuaternary,
        modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
    )
}

@Composable
private fun ExpiryAlarmRow(
    checked: Boolean,
    onIntent: (SettingNotificationIntent) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(NOTIFICATION_ROW_HEIGHT)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(MyPageR.string.setting_notification_expiry_alarm),
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.weight(1f))
        NeveraSwitch(
            checked = checked,
            onCheckedChange = { enabled ->
                val isPermissionGranted =
                    DefaultPermissionChecker.isGranted(context, AppPermission.Notification)
                onIntent(SettingNotificationIntent.ExpiryAlarmToggled(enabled, isPermissionGranted))
            },
        )
    }
}

@Composable
private fun AlarmTimeRow(
    formattedTime: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(NOTIFICATION_ROW_HEIGHT)
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(MyPageR.string.setting_notification_alarm_time),
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = formattedTime,
            style = NeveraTheme.typography.bodyLarge,
            color = if (isEnabled) {
                NeveraTheme.colors.textTertiary
            } else {
                NeveraTheme.colors.textDisabled
            },
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap4))
        Icon(
            painter = NeveraIcons.ChevronSmallRight,
            contentDescription = null,
            tint = if (isEnabled) {
                NeveraTheme.colors.iconSecondary
            } else {
                NeveraTheme.colors.iconDisabled
            },
            modifier = Modifier.size(NeveraTheme.iconSize.xSmall),
        )
    }
}

@Preview(name = "SettingNotificationContent - OFF", showBackground = true, widthDp = 360)
@Composable
private fun SettingNotificationContentOffPreview() {
    NeveraTheme {
        SettingNotificationContent(
            uiState = SettingNotificationUiState(isExpiryAlarmEnabled = false),
            onIntent = {},
        )
    }
}

@Preview(name = "SettingNotificationContent - ON", showBackground = true, widthDp = 360)
@Composable
private fun SettingNotificationContentOnPreview() {
    NeveraTheme {
        SettingNotificationContent(
            uiState = SettingNotificationUiState(isExpiryAlarmEnabled = true),
            onIntent = {},
        )
    }
}
