package com.anddd.nevera.core.ui.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val ExpiryDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Composable
fun ExpiryDateFieldRow(
    expiryDate: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldRowLabel(stringResource(R.string.field_label_expiry_date))
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(FieldRowDimension.ExpiryDateRowHeight)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(NeveraTheme.spacing.padding12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = expiryDate?.format(ExpiryDateFormatter) ?: stringResource(R.string.field_expiry_date_placeholder),
                modifier = Modifier.weight(1f),
                color = if (expiryDate != null) NeveraTheme.colors.textSecondary
                else NeveraTheme.colors.textDisabled,
                style = NeveraTheme.typography.bodyLarge,
            )
            Icon(
                painter = NeveraIcons.ChevronSmallRight,
                contentDescription = null,
                modifier = Modifier.size(NeveraTheme.iconSize.small),
                tint = NeveraTheme.colors.iconCaption,
            )
        }
    }
}
