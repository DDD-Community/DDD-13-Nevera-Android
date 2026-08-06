package com.anddd.nevera.core.designsystem.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

fun ComposeContentTestRule.setNeveraContent(content: @Composable () -> Unit) {
    setContent {
        NeveraTheme {
            content()
        }
    }
}
