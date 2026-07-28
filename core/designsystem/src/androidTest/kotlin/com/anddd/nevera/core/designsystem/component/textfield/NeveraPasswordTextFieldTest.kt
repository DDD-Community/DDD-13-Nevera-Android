package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Rule
import org.junit.Test

class NeveraPasswordTextFieldTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `기본 상태에서는 비밀번호 표시 아이콘을 노출한다`() {
        composeRule.setNeveraContent {
            NeveraPasswordTextField(value = "secret", onValueChange = {})
        }

        composeRule.onNodeWithContentDescription("비밀번호 표시").assertIsDisplayed()
    }

    @Test
    fun `비밀번호 표시 아이콘을 누르면 숨기기 아이콘으로 바뀐다`() {
        composeRule.setNeveraContent {
            NeveraPasswordTextField(value = "secret", onValueChange = {})
        }

        composeRule.onNodeWithContentDescription("비밀번호 표시").performClick()

        composeRule.onNodeWithContentDescription("비밀번호 숨기기").assertIsDisplayed()
    }

    @Test
    fun `useIcon이 false이면 비밀번호 표시 아이콘을 노출하지 않는다`() {
        composeRule.setNeveraContent {
            NeveraPasswordTextField(
                value = "secret",
                onValueChange = {},
                useIcon = false,
            )
        }

        composeRule.onNodeWithContentDescription("비밀번호 표시").assertDoesNotExist()
    }

    @Test
    fun `비밀번호 표시 아이콘을 누르면 평문 텍스트를 표시한다`() {
        composeRule.setNeveraContent {
            NeveraPasswordTextField(value = "secret", onValueChange = {})
        }

        composeRule.onNodeWithContentDescription("비밀번호 표시").performClick()

        composeRule.onNodeWithText("secret").assertIsDisplayed()
    }
}
