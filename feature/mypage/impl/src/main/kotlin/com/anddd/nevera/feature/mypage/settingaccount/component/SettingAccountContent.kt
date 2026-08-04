package com.anddd.nevera.feature.mypage.settingaccount.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountIntent
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountUiState
import com.anddd.nevera.feature.mypage.R as MyPageR

@Composable
internal fun SettingAccountContent(
    uiState: SettingAccountUiState,
    onIntent: (SettingAccountIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NeveraTheme.colors.backgroundPrimary,
        topBar = {
            NeveraAppBar(
                title = stringResource(MyPageR.string.setting_account_title),
                navigation = NeveraAppBarNavigation.Back(
                    onClick = { onIntent(SettingAccountIntent.NavigateBack) }
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
                SettingAccountItem(
                    label = stringResource(MyPageR.string.setting_account_logout),
                    onClick = { onIntent(SettingAccountIntent.LogoutClicked) },
                )
                SettingAccountItem(
                    label = stringResource(MyPageR.string.setting_account_withdraw),
                    onClick = { onIntent(SettingAccountIntent.WithdrawClicked) },
                )
            }

            if (uiState.showLogoutDialog) {
                NeveraConfirmDialog(
                    title = stringResource(MyPageR.string.setting_account_logout_dialog_title),
                    positive = stringResource(MyPageR.string.setting_account_logout_confirm),
                    negative = stringResource(MyPageR.string.setting_account_dialog_dismiss),
                    onNegative = { onIntent(SettingAccountIntent.CancelLogoutClicked) },
                    onPositive = { onIntent(SettingAccountIntent.ConfirmLogoutClicked) },
                    negativeButtonColor = NeveraButtonColor.Secondary,
                )
            }

            if (uiState.showWithdrawDialog) {
                NeveraConfirmDialog(
                    title = stringResource(MyPageR.string.setting_account_withdraw_dialog_title),
                    subtitle = stringResource(MyPageR.string.setting_account_withdraw_dialog_subtitle),
                    positive = stringResource(MyPageR.string.setting_account_withdraw_confirm),
                    negative = stringResource(MyPageR.string.setting_account_dialog_dismiss),
                    onNegative = { onIntent(SettingAccountIntent.CancelWithdrawClicked) },
                    onPositive = { onIntent(SettingAccountIntent.ConfirmWithdrawClicked) },
                    negativeButtonColor = NeveraButtonColor.Secondary,
                    positiveButtonColor = NeveraButtonColor.Danger,
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
private fun SettingAccountContentPreview() {
    NeveraTheme {
        SettingAccountContent(
            uiState = SettingAccountUiState(),
            onIntent = {},
        )
    }
}
