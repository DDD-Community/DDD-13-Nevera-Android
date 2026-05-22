package com.anddd.nevera.core.designsystem.component.datepicker

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Nevera 디자인 시스템 날짜 선택 다이얼로그
 *
 * Material3 [DatePickerDialog]에 Nevera 디자인 토큰을 적용한 래퍼 컴포넌트입니다.
 *
 * ### 타임존 계약
 * Material3 [rememberDatePickerState]는 내부적으로 UTC epoch millis를 사용합니다.
 * 이 컴포넌트는 입·출력 모두 UTC 자정 기준으로 변환하며, 반환 타입인 [LocalDate]에는
 * 타임존 정보가 포함되지 않습니다.
 * 서버에 timestamp를 전달해야 하는 경우, 호출 측(ViewModel/Repository)에서
 * UTC 기준으로 변환하는 책임을 가집니다.
 *
 * @param selectedDate   현재 선택된 날짜 (null = 미설정)
 * @param onDateSelected 확인 탭 시 선택 날짜 전달 — 타임존 없는 순수 [LocalDate] 반환
 * @param onDismiss      닫기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraDatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli(),
    )

    val colors = DatePickerDefaults.colors(
        containerColor = NeveraTheme.colors.surfacePrimary,
        selectedDayContainerColor = NeveraTheme.colors.primaryNormal,
        selectedDayContentColor = NeveraTheme.colors.surfacePrimary,
        todayContentColor = NeveraTheme.colors.primaryNormal,
        todayDateBorderColor = NeveraTheme.colors.primaryNormal,
        selectedYearContainerColor = NeveraTheme.colors.primaryNormal,
        selectedYearContentColor = NeveraTheme.colors.surfacePrimary,
        dayContentColor = NeveraTheme.colors.textPrimary,
        navigationContentColor = NeveraTheme.colors.iconPrimary,
        titleContentColor = NeveraTheme.colors.textSecondary,
        headlineContentColor = NeveraTheme.colors.textPrimary,
        dateTextFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeveraTheme.colors.primaryNormal,
            focusedLabelColor = NeveraTheme.colors.primaryNormal,
            cursorColor = NeveraTheme.colors.primaryNormal,
            unfocusedBorderColor = NeveraTheme.colors.borderNormal,
            unfocusedLabelColor = NeveraTheme.colors.textSecondary,
            focusedTextColor = NeveraTheme.colors.textPrimary,
            unfocusedTextColor = NeveraTheme.colors.textPrimary,
        ),
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        )
                    }
                    onDismiss()
                },
                enabled = datePickerState.selectedDateMillis != null,
            ) {
                Text(
                    text = stringResource(R.string.nevera_date_picker_confirm),
                    color = NeveraTheme.colors.primaryNormal,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.nevera_date_picker_dismiss),
                    color = NeveraTheme.colors.primaryNormal,
                )
            }
        },
        colors = colors,
    ) {
        DatePicker(
            state = datePickerState,
            colors = colors,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, locale = "ko")
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, locale = "ko")
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
