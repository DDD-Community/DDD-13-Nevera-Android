package com.anddd.nevera.core.designsystem.component.datepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anddd.nevera.core.designsystem.component.datepicker.internal.CalendarGrid
import com.anddd.nevera.core.designsystem.component.datepicker.internal.DatePickerHeader
import com.anddd.nevera.core.designsystem.component.datepicker.internal.DayOfWeekHeader
import com.anddd.nevera.core.designsystem.component.datepicker.internal.DialogButtons
import com.anddd.nevera.core.designsystem.component.datepicker.internal.MonthNavigationRow
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.LocalDate
import java.time.YearMonth

/**
 * TODO :: 디자인 수치들이 이상해서, 확인요청해둔 상태
 * 날짜 선택 다이얼로그
 *
 * @param selectedDate   현재 선택된 날짜 (null = 미설정)
 * @param onDateSelected 확인 탭 시 선택 날짜 전달
 * @param onDismiss      닫기 콜백
 */
@Composable
fun NeveraDatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val today = LocalDate.now()
    var displayedYearMonth by remember {
        mutableStateOf(YearMonth.from(selectedDate ?: today))
    }
    var tempSelected by remember(selectedDate) { mutableStateOf(selectedDate) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NeveraTheme.spacing.padding16),
            shape = RoundedCornerShape(NeveraTheme.radius.large),
            colors = CardDefaults.cardColors(containerColor = NeveraTheme.colors.surfacePrimary),
        ) {
            Column(modifier = Modifier.padding(top = NeveraTheme.spacing.padding16)) {
                DatePickerHeader(tempSelected = tempSelected)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = NeveraTheme.spacing.gap8),
                    color = NeveraTheme.colors.borderNormal,
                )
                MonthNavigationRow(
                    displayedYearMonth = displayedYearMonth,
                    onPrevMonth = { displayedYearMonth = displayedYearMonth.minusMonths(1) },
                    onNextMonth = { displayedYearMonth = displayedYearMonth.plusMonths(1) },
                )
                DayOfWeekHeader()
                CalendarGrid(
                    displayedYearMonth = displayedYearMonth,
                    today = today,
                    selectedDate = tempSelected,
                    onDateClick = { tempSelected = it },
                )
                DialogButtons(
                    tempSelected = tempSelected,
                    onDismiss = onDismiss,
                    onConfirm = {
                        tempSelected?.let(onDateSelected)
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NeveraDatePickerDialogSelectedPreview() {
    NeveraTheme {
        NeveraDatePickerDialog(
            selectedDate = LocalDate.of(2026, 12, 17),
            onDateSelected = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NeveraDatePickerDialogEmptyPreview() {
    NeveraTheme {
        NeveraDatePickerDialog(
            selectedDate = null,
            onDateSelected = {},
            onDismiss = {},
        )
    }
}
