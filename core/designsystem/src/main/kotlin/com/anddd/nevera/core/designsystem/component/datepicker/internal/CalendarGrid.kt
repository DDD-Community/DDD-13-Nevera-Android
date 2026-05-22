package com.anddd.nevera.core.designsystem.component.datepicker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun CalendarGrid(
    displayedYearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
) {
    val calendarDays = remember(displayedYearMonth) { getCalendarDays(displayedYearMonth) }
    val weeks = calendarDays.chunked(7)

    Column(verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap4)) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        today = today,
                        selectedDate = selectedDate,
                        onClick = onDateClick,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(7 - week.size) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    today: LocalDate,
    selectedDate: LocalDate?,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (date == null) {
        Box(modifier = modifier.aspectRatio(1f))
        return
    }

    val isToday = date == today
    val isSelected = date == selectedDate

    Box(
        modifier = modifier.aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> NeveraTheme.colors.primaryNormal
                    else -> Color.Transparent
                },
            )
            .then(
                if (isToday && !isSelected)
                    Modifier.border(1.dp, NeveraTheme.colors.primaryNormal, CircleShape)
                else Modifier,
            )
            .clickable(role = Role.Button) { onClick(date) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = NeveraTheme.typography.bodySmall,
            color = when {
                isSelected -> NeveraTheme.colors.surfacePrimary
                isToday -> NeveraTheme.colors.primaryNormal
                else -> NeveraTheme.colors.textPrimary
            },
        )
    }
}

/**
 * 해당 월의 달력 날짜 목록을 생성한다.
 * 월의 첫 번째 요일(일=0, 월=1, ..., 토=6)에 맞게 앞 칸을 null로 채운다.
 */
private fun getCalendarDays(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()
    // DayOfWeek: MONDAY=1 ~ SUNDAY=7, 일요일을 0으로 변환
    val offset = firstDay.dayOfWeek.value % 7
    val days = mutableListOf<LocalDate?>()
    repeat(offset) { days.add(null) }
    var current = firstDay
    while (!current.isAfter(lastDay)) {
        days.add(current)
        current = current.plusDays(1)
    }
    return days
}

private val previewToday = LocalDate.of(2026, 12, 10)
private val previewYearMonth = YearMonth.of(2026, 12)

@Preview(
    name = "CalendarGrid - 날짜 선택됨",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun CalendarGridSelectedPreview() {
    NeveraTheme {
        CalendarGrid(
            displayedYearMonth = previewYearMonth,
            today = previewToday,
            selectedDate = LocalDate.of(2026, 12, 17),
            onDateClick = {},
        )
    }
}

@Preview(
    name = "CalendarGrid - 미선택",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun CalendarGridEmptyPreview() {
    NeveraTheme {
        CalendarGrid(
            displayedYearMonth = previewYearMonth,
            today = previewToday,
            selectedDate = null,
            onDateClick = {},
        )
    }
}