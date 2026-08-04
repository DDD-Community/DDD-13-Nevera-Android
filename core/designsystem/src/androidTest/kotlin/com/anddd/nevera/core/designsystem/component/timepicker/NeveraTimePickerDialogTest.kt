package com.anddd.nevera.core.designsystem.component.timepicker

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraTimePickerDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `자정 초기값에서 완료하면 0시 0분을 전달한다`() {
        var selectedTime: Pair<Int, Int>? = null
        composeRule.setNeveraContent {
            NeveraTimePickerDialog(
                initialHour = 0,
                initialMinute = 0,
                onTimeSelected = { hour, minute -> selectedTime = hour to minute },
                onDismiss = {},
            )
        }

        composeRule.onNodeWithText("완료").performClick()

        assertEquals(0 to 0, selectedTime)
    }

    @Test
    fun `정오 초기값에서 완료하면 12시 0분을 전달한다`() {
        var selectedTime: Pair<Int, Int>? = null
        composeRule.setNeveraContent {
            NeveraTimePickerDialog(
                initialHour = 12,
                initialMinute = 0,
                onTimeSelected = { hour, minute -> selectedTime = hour to minute },
                onDismiss = {},
            )
        }

        composeRule.onNodeWithText("완료").performClick()

        assertEquals(12 to 0, selectedTime)
    }

    @Test
    fun `오후 여섯시 반 초기값에서 완료하면 18시 30분을 전달한다`() {
        var selectedTime: Pair<Int, Int>? = null
        composeRule.setNeveraContent {
            NeveraTimePickerDialog(
                initialHour = 18,
                initialMinute = 30,
                onTimeSelected = { hour, minute -> selectedTime = hour to minute },
                onDismiss = {},
            )
        }

        composeRule.onNodeWithText("완료").performClick()

        assertEquals(18 to 30, selectedTime)
    }
}
