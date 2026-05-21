package com.anddd.nevera.core.designsystem.component.datepicker.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun DatePickerHeader(
    modifier: Modifier = Modifier,
    tempSelected: LocalDate?,
) {
    Row(
        modifier = modifier.height(120.dp)
            .padding(
                start = NeveraTheme.spacing.padding24,
                top = NeveraTheme.spacing.padding16,
                end = NeveraTheme.spacing.padding12,
                bottom = NeveraTheme.spacing.gap12
            ),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = stringResource(R.string.nevera_date_picker_title),
                modifier = Modifier.fillMaxWidth(),
                style = NeveraTheme.typography.captionMedium,
                color = NeveraTheme.colors.textTertiary,
            )
            // TODO :: 원래 36dp인데, 토큰에 정의된 값이 없음.
            Spacer(Modifier.weight(1f))
            Text(
                text = tempSelected?.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    ?: stringResource(R.string.nevera_date_picker_placeholder),
                modifier = Modifier.fillMaxWidth(),
                style = NeveraTheme.typography.headlineLarge,
                color = NeveraTheme.colors.textPrimary,
            )
        }
        Spacer(Modifier.height(NeveraTheme.spacing.gap8))
        IconButton(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_date_pencil),
                contentDescription = null,
                tint = NeveraTheme.colors.iconPrimary,
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
            )
        }
    }
}

@Preview(
    name = "DatePickerHeader - 날짜 선택됨",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun DatePickerHeaderSelectedPreview() {
    NeveraTheme {
        DatePickerHeader(tempSelected = LocalDate.of(2026, 12, 17))
    }
}

@Preview(
    name = "DatePickerHeader - 미선택",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun DatePickerHeaderEmptyPreview() {
    NeveraTheme {
        DatePickerHeader(tempSelected = null)
    }
}