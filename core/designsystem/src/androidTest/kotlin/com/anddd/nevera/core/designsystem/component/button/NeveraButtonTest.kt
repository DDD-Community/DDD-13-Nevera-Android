package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraButtonTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `Filled 버튼은 label을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraFilledButton(label = "확인", onClick = {})
        }

        composeRule.onNodeWithText("확인").assertIsDisplayed()
    }

    @Test
    fun `텍스트 버튼을 누르면 클릭 콜백을 호출한다`() {
        var filledClickCount = 0
        var outlinedClickCount = 0
        var ghostClickCount = 0
        var weakClickCount = 0
        composeRule.setNeveraContent {
            Column {
                NeveraFilledButton(label = "Filled", onClick = { filledClickCount++ })
                NeveraOutlinedButton(label = "Outlined", onClick = { outlinedClickCount++ })
                NeveraGhostButton(label = "Ghost", onClick = { ghostClickCount++ })
                NeveraWeakButton(label = "Weak", onClick = { weakClickCount++ })
            }
        }

        composeRule.onNode(hasText("Filled") and hasClickAction()).performClick()
        composeRule.onNode(hasText("Outlined") and hasClickAction()).performClick()
        composeRule.onNode(hasText("Ghost") and hasClickAction()).performClick()
        composeRule.onNode(hasText("Weak") and hasClickAction()).performClick()

        assertEquals("Filled click count", 1, filledClickCount)
        assertEquals("Outlined click count", 1, outlinedClickCount)
        assertEquals("Ghost click count", 1, ghostClickCount)
        assertEquals("Weak click count", 1, weakClickCount)
    }

    @Test
    fun `disabled 텍스트 버튼은 비활성 상태이고 눌러도 콜백을 호출하지 않는다`() {
        var disabledClickCount = 0
        composeRule.setNeveraContent {
            NeveraFilledButton(
                label = "비활성",
                onClick = { disabledClickCount++ },
                enabled = false,
            )
        }

        composeRule.onNode(hasText("비활성") and hasClickAction()).assertIsNotEnabled()

        composeRule.onNode(hasText("비활성") and hasClickAction()).performClick()

        assertEquals("Disabled click count", 0, disabledClickCount)
    }
}
