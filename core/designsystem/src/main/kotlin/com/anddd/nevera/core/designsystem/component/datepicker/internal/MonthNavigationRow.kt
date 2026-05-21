package com.anddd.nevera.core.designsystem.component.datepicker.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.YearMonth

@Composable
internal fun MonthNavigationRow(
    modifier: Modifier = Modifier,
    displayedYearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = modifier.heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${displayedYearMonth.year}년 ${displayedYearMonth.monthValue}월",
            style = NeveraTheme.typography.bodyLarge,
            color = NeveraTheme.colors.textPrimary,
            modifier = Modifier.padding(start = NeveraTheme.spacing.padding16),
        )
        Spacer(Modifier.width(NeveraTheme.spacing.gap8))
        // TODO :: icon이 다운로드 받을수 없음, 추후 수정 필요
        Icon(
            painter = NeveraIcons.ChevronSmallDown,
            contentDescription = null,
            tint = NeveraTheme.colors.iconTertiary,
            modifier = Modifier.size(NeveraTheme.iconSize.small),
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onPrevMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_date_arrow_left),
                contentDescription = stringResource(R.string.nevera_date_picker_prev_month),
                tint = NeveraTheme.colors.iconPrimary,
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
            )
        }
        IconButton(onClick = onNextMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_date_arrow_right),
                contentDescription = stringResource(R.string.nevera_date_picker_next_month),
                tint = NeveraTheme.colors.iconPrimary,
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
            )
        }
    }
}

@Preview(
    name = "MonthNavigationRow",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun MonthNavigationRowPreview() {
    NeveraTheme {
        MonthNavigationRow(
            displayedYearMonth = YearMonth.of(2026, 12),
            onPrevMonth = {},
            onNextMonth = {},
        )
    }
}