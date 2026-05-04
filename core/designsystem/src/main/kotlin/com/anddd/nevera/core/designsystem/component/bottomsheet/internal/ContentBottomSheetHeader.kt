package com.anddd.nevera.core.designsystem.component.bottomsheet.internal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun ContentBottomSheetHeader(
    title: String,
    subtitle: String,
) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 27.dp)
            .padding(
                start = NeveraTheme.spacing.padding7,
                top = NeveraTheme.spacing.padding6,
                end = NeveraTheme.spacing.padding7,
            ),
        color = NeveraTheme.colors.textSecondary,
        style = NeveraTheme.typography.titleLarge,
    )
    Text(
        text = subtitle,
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 21.dp)
            .padding(
                start = NeveraTheme.spacing.padding7,
                end = NeveraTheme.spacing.padding7,
                bottom = NeveraTheme.spacing.padding6
            ),
        color = NeveraTheme.colors.textQuaternary,
        style = NeveraTheme.typography.bodySmall,
    )
}