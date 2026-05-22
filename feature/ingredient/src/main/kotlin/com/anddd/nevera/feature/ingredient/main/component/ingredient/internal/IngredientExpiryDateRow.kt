package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun IngredientExpiryDateRow(
    expiryDate: LocalDate?,
    onClick: () -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_expiry)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = NeveraTheme.spacing.padding12,
                    vertical = NeveraTheme.spacing.padding12,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = expiryDate?.format(dateFormatter)
                       ?: stringResource(R.string.ingredient_item_placeholder_date),
                style = NeveraTheme.typography.bodyMedium,
                color = if (expiryDate != null) NeveraTheme.colors.textPrimary
                        else NeveraTheme.colors.textTertiary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = NeveraIcons.ChevronSmallRight,
                contentDescription = null,
                tint = NeveraTheme.colors.iconSecondary,
                modifier = Modifier.size(NeveraTheme.iconSize.small),
            )
        }
    }
}
