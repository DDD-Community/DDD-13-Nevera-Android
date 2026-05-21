package com.anddd.nevera.core.designsystem.component.datepicker.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun DayOfWeekHeader() {
    val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = NeveraTheme.typography.captionMedium,
                color = NeveraTheme.colors.textTertiary,
            )
        }
    }
}

@Preview(
    name = "DayOfWeekHeader",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun DayOfWeekHeaderPreview() {
    NeveraTheme {
        DayOfWeekHeader()
    }
}