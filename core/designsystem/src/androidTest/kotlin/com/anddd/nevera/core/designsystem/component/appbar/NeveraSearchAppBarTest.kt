package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraSearchAppBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `검색 슬롯을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraSearchAppBar(
                searchBar = { Text("검색 영역") },
            )
        }

        composeRule.onNodeWithText("검색 영역").assertIsDisplayed()
    }

    @Test
    fun `내비게이션과 액션을 함께 클릭할 수 있다`() {
        var navigationClickCount = 0
        var actionClickCount = 0
        composeRule.setNeveraContent {
            NeveraSearchAppBar(
                navigation = NeveraAppBarNavigation.Back { navigationClickCount++ },
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = NeveraIcons.Close,
                        contentDescription = "닫기",
                        onClick = { actionClickCount++ },
                    ),
                ),
                searchBar = { Text("검색 영역") },
            )
        }

        composeRule.onNodeWithContentDescription("뒤로가기").performClick()
        composeRule.onNodeWithContentDescription("닫기").performClick()

        assertEquals(1, navigationClickCount)
        assertEquals(1, actionClickCount)
    }
}
