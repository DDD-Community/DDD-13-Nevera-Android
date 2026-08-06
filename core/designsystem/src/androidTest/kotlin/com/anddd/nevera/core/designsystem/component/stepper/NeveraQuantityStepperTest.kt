package com.anddd.nevera.core.designsystem.component.stepper

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraQuantityStepperTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `현재 수량을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 5,
                onDecrease = {},
                onIncrease = {},
            )
        }

        composeRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun `최솟값에서는 감소 버튼이 비활성이다`() {
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 1,
                onDecrease = {},
                onIncrease = {},
            )
        }

        composeRule.onNodeWithContentDescription("수량 1개 감소").assertIsNotEnabled()
    }

    @Test
    fun `최댓값에서는 증가 버튼이 비활성이다`() {
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 999,
                onDecrease = {},
                onIncrease = {},
            )
        }

        composeRule.onNodeWithContentDescription("수량 1개 증가").assertIsNotEnabled()
    }

    @Test
    fun `중간값에서 감소 버튼을 누르면 감소 콜백을 호출한다`() {
        var decreaseCount = 0
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 5,
                onDecrease = { decreaseCount++ },
                onIncrease = {},
            )
        }

        composeRule.onNodeWithContentDescription("수량 1개 감소").performClick()

        assertEquals(1, decreaseCount)
    }

    @Test
    fun `중간값에서 증가 버튼을 누르면 증가 콜백을 호출한다`() {
        var increaseCount = 0
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 5,
                onDecrease = {},
                onIncrease = { increaseCount++ },
            )
        }

        composeRule.onNodeWithContentDescription("수량 1개 증가").performClick()

        assertEquals(1, increaseCount)
    }
}
