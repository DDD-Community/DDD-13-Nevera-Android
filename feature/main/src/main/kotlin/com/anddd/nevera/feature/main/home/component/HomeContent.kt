package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun HomeContent(
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "홈",
            style = NeveraTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그아웃")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onWithdrawClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원탈퇴")
        }
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    NeveraTheme {
        HomeContent(
            onLogoutClick = {},
            onWithdrawClick = {}
        )
    }
}