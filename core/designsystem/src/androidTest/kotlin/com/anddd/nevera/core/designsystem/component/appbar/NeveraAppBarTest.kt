package com.anddd.nevera.core.designsystem.component.appbar

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

class NeveraAppBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `제목을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraAppBar(title = "타이틀")
        }

        composeRule.onNodeWithText("타이틀").assertIsDisplayed()
    }

    @Test
    fun `뒤로가기 내비게이션을 누르면 콜백을 호출한다`() {
        var clickCount = 0
        composeRule.setNeveraContent {
            NeveraAppBar(
                title = "타이틀",
                navigation = NeveraAppBarNavigation.Back { clickCount++ },
            )
        }

        composeRule.onNodeWithContentDescription("뒤로가기").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun `닫기 내비게이션을 누르면 콜백을 호출한다`() {
        var clickCount = 0
        composeRule.setNeveraContent {
            NeveraAppBar(
                navigation = NeveraAppBarNavigation.Close { clickCount++ },
            )
        }

        composeRule.onNodeWithContentDescription("닫기").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun `메뉴 내비게이션을 누르면 콜백을 호출한다`() {
        var clickCount = 0
        composeRule.setNeveraContent {
            NeveraAppBar(
                navigation = NeveraAppBarNavigation.Menu { clickCount++ },
            )
        }

        composeRule.onNodeWithContentDescription("메뉴").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun `텍스트 액션을 누르면 콜백을 호출한다`() {
        var clickCount = 0
        composeRule.setNeveraContent {
            NeveraAppBar(
                action = NeveraAppBarAction.Text(
                    label = "완료",
                    onClick = { clickCount++ },
                ),
            )
        }

        composeRule.onNodeWithText("완료").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun `아이콘 액션을 누르면 콜백을 호출한다`() {
        var clickCount = 0
        composeRule.setNeveraContent {
            NeveraAppBar(
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = NeveraIcons.Search,
                        contentDescription = "검색",
                        onClick = { clickCount++ },
                    ),
                ),
            )
        }

        composeRule.onNodeWithContentDescription("검색").performClick()

        assertEquals(1, clickCount)
    }
}
