package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.main.model.MyPageIntent
import com.anddd.nevera.feature.mypage.main.model.MyPageUiState

@Composable
internal fun MyPageContent(
    uiState: MyPageUiState,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        // TODO: UI 구현
        Text(text = "MyPage")
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
