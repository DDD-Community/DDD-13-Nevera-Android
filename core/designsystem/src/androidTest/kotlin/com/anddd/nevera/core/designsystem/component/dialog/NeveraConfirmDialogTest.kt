package com.anddd.nevera.core.designsystem.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraConfirmDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `제목과 설명과 버튼을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraConfirmDialog(
                title = "삭제할까요?",
                subtitle = "삭제하면 되돌릴 수 없어요",
                positive = "삭제",
                negative = "취소",
                onPositive = {},
                onNegative = {},
            )
        }

        composeRule.onNodeWithText("삭제할까요?").assertIsDisplayed()
        composeRule.onNodeWithText("삭제하면 되돌릴 수 없어요").assertIsDisplayed()
        composeRule.onNodeWithText("삭제").assertIsDisplayed()
        composeRule.onNodeWithText("취소").assertIsDisplayed()
    }

    @Test
    fun `positive 버튼을 누르면 positive 콜백을 호출한다`() {
        var positiveCount = 0
        var negativeCount = 0
        composeRule.setNeveraContent {
            NeveraConfirmDialog(
                title = "삭제할까요?",
                positive = "삭제",
                negative = "취소",
                onPositive = { positiveCount++ },
                onNegative = { negativeCount++ },
            )
        }

        composeRule.onNodeWithText("삭제").performClick()

        assertEquals(1, positiveCount)
        assertEquals(0, negativeCount)
    }

    @Test
    fun `negative 버튼을 누르면 negative 콜백을 호출한다`() {
        var positiveCount = 0
        var negativeCount = 0
        composeRule.setNeveraContent {
            NeveraConfirmDialog(
                title = "삭제할까요?",
                positive = "삭제",
                negative = "취소",
                onPositive = { positiveCount++ },
                onNegative = { negativeCount++ },
            )
        }

        composeRule.onNodeWithText("취소").performClick()

        assertEquals(0, positiveCount)
        assertEquals(1, negativeCount)
    }
}
