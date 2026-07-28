package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Rule
import org.junit.Test

class NeveraEmailTextFieldTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `이메일 필드는 안내 문구와 입력 값을 표시한다`() {
        composeRule.setNeveraContent {
            var value by remember { mutableStateOf("") }
            NeveraEmailTextField(
                value = value,
                onValueChange = { value = it },
                config = NeveraTextFieldConfig(
                    heading = "이메일",
                    placeholder = "email@example.com",
                    description = "로그인에 사용할 이메일",
                ),
            )
        }

        composeRule.onNodeWithText("이메일").assertIsDisplayed()
        composeRule.onNodeWithText("email@example.com").assertIsDisplayed()
        composeRule.onNodeWithText("로그인에 사용할 이메일").assertIsDisplayed()

        composeRule.onNode(hasSetTextAction()).performTextInput("hello@nevera.dev")

        composeRule.onNodeWithText("hello@nevera.dev").assertIsDisplayed()
    }
}
