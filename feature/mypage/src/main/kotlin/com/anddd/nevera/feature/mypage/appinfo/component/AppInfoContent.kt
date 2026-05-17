package com.anddd.nevera.feature.mypage.appinfo.component

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
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoIntent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoUiModel
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoUiState
import com.anddd.nevera.feature.mypage.R as MyPageR

@Composable
internal fun AppInfoContent(
    uiState: AppInfoUiState,
    onIntent: (AppInfoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NeveraTheme.colors.backgroundPrimary,
        topBar = {
            NeveraAppBar(
                title = stringResource(MyPageR.string.app_info_title),
                navigation = NeveraAppBarNavigation.Back(
                    onClick = { onIntent(AppInfoIntent.NavigateBack) }
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
                AppInfoItem(
                    label = stringResource(MyPageR.string.app_info_terms),
                    onClick = { onIntent(AppInfoIntent.TermsClicked) },
                )
                AppInfoItem(
                    label = stringResource(MyPageR.string.app_info_privacy_policy),
                    onClick = { onIntent(AppInfoIntent.PrivacyPolicyClicked) },
                )
                AppInfoVersionItem(
                    label = stringResource(MyPageR.string.app_info_version),
                    versionName = uiState.appInfo.versionName,
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
private fun AppInfoContentPreview() {
    NeveraTheme {
        AppInfoContent(
            uiState = AppInfoUiState(appInfo = AppInfoUiModel(versionName = "V1.1.0")),
            onIntent = {},
        )
    }
}
