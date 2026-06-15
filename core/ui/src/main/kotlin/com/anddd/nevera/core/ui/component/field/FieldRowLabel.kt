package com.anddd.nevera.core.ui.component.field

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun FieldRowLabel(label: String) {
    Text(
        text = label,
        modifier = Modifier.width(FieldRowDimension.FieldLabelWidth),
        color = NeveraTheme.colors.textCaption,
        style = NeveraTheme.typography.titleXSmall,
    )
}
