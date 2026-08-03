package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Rule
import org.junit.Test

class NeveraTextFieldTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `heading placeholder description을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraTextField(
                value = "",
                onValueChange = {},
                config = NeveraTextFieldConfig(
                    heading = "이름",
                    placeholder = "입력",
                    description = "도움말",
                ),
            )
        }

        composeRule.onNodeWithText("이름").assertIsDisplayed()
        composeRule.onNodeWithText("입력").assertIsDisplayed()
        composeRule.onNodeWithText("도움말").assertIsDisplayed()
    }

    @Test
    fun `텍스트를 입력하면 value 변경 콜백을 통해 값을 표시한다`() {
        composeRule.setNeveraContent {
            var value by remember { mutableStateOf("") }
            NeveraTextField(
                value = value,
                onValueChange = { value = it },
                config = NeveraTextFieldConfig(placeholder = "입력"),
            )
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("abc")

        composeRule.onNodeWithText("abc").assertIsDisplayed()
    }

    @Test
    fun `Positive 상태이고 값이 있으면 올바른 입력 아이콘을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraTextField(
                value = "abc",
                onValueChange = {},
                config = NeveraTextFieldConfig(state = NeveraTextFieldState.Positive),
            )
        }

        composeRule.onNodeWithContentDescription("입력이 올바릅니다").assertIsDisplayed()
    }

    @Test
    fun `Positive 상태라도 값이 비어 있으면 올바른 입력 아이콘을 표시하지 않는다`() {
        composeRule.setNeveraContent {
            NeveraTextField(
                value = "",
                onValueChange = {},
                config = NeveraTextFieldConfig(state = NeveraTextFieldState.Positive),
            )
        }

        composeRule.onNodeWithContentDescription("입력이 올바릅니다").assertDoesNotExist()
    }

    @Test
    fun `Negative 상태이면 확인 필요 아이콘을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraTextField(
                value = "",
                onValueChange = {},
                config = NeveraTextFieldConfig(state = NeveraTextFieldState.Negative),
            )
        }

        composeRule.onNodeWithContentDescription("입력을 확인하세요").assertIsDisplayed()
    }
}
