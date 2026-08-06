package com.anddd.nevera.core.designsystem.component.datepicker

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraDatePickerDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `선택된 날짜가 있으면 확인 시 날짜를 전달하고 닫는다`() {
        var selectedDate: LocalDate? = null
        var dismissCount = 0
        composeRule.setNeveraContent {
            NeveraDatePickerDialog(
                selectedDate = LocalDate.of(2026, 12, 17),
                onDateSelected = { selectedDate = it },
                onDismiss = { dismissCount++ },
            )
        }

        composeRule.onNodeWithText("확인").performClick()

        assertEquals(LocalDate.of(2026, 12, 17), selectedDate)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `선택된 날짜가 없으면 확인 버튼이 비활성이다`() {
        composeRule.setNeveraContent {
            NeveraDatePickerDialog(
                selectedDate = null,
                onDateSelected = {},
                onDismiss = {},
            )
        }

        composeRule.onNode(hasText("확인") and hasClickAction()).assertIsNotEnabled()
    }
}
