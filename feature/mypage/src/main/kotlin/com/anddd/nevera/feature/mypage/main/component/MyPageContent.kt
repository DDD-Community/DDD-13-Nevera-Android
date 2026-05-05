package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraDisplayAppBar
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState
import com.anddd.nevera.feature.mypage.main.model.SettingItemType
import com.anddd.nevera.feature.mypage.main.model.SettingItemUiModel

@Composable
internal fun MyPageContent(
    uiState: MyPageUiState,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary),
        topBar = {
            NeveraDisplayAppBar(
                title = "마이",
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = NeveraIcons.Search,
                        contentDescription = "검색",
                        onClick = {},
                    )
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(NeveraTheme.colors.backgroundPrimary)
                .padding(innerPadding),
        ) {
            ProfileContent(
                profileImage = null,
                name = "홍길동",
                email = "hong@example.com",
            )

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
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MyPageContentPreview() {
    NeveraTheme {
        MyPageContent(
            uiState = MyPageUiState(),
            onIntent = {},
        )
    }
}
