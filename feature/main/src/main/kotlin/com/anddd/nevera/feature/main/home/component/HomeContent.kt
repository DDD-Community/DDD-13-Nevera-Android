package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraLogoAppBar
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.main.home.model.HomeUiState

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
) {
    Scaffold(
        topBar = {
            NeveraLogoAppBar(
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = NeveraIcons.Bell,
                        contentDescription = "알림",
                        onClick = {},
                    ),
                ),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "홈",
                    style = NeveraTheme.typography.headlineMedium
                )
            }
        }

        if (uiState.isLoading) {
            LoadingContent()
        }
    }

}

@Preview
@Composable
private fun HomeContentPreview() {
    NeveraTheme {
        HomeContent(
            uiState = HomeUiState(),
        )
    }
}