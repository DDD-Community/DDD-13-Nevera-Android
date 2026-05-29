package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shadow.neveraShadow
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter

@Composable
internal fun FridgeFilterTabRow(
    selectedFilter: StorageLocationFilter,
    onFilterSelected: (StorageLocationFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NeveraTheme.colors.surfaceSecondary, RoundedCornerShape(NeveraTheme.radius.medium))
            .padding(NeveraTheme.spacing.padding4),
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap0),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FridgeFilterTab(
            label = stringResource(R.string.fridge_filter_all),
            selected = selectedFilter is StorageLocationFilter.All,
            onClick = { onFilterSelected(StorageLocationFilter.All) },
            modifier = Modifier.weight(1f),
        )
        StorageLocation.entries.forEach { location ->
            FridgeFilterTab(
                label = stringResource(location.labelRes),
                selected = selectedFilter is StorageLocationFilter.Specific &&
                    selectedFilter.location == location,
                onClick = { onFilterSelected(StorageLocationFilter.Specific(location)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FridgeFilterTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = NeveraTheme.typography.titleXSmall,
        color = if (selected) NeveraTheme.colors.textSecondary else NeveraTheme.colors.textCaption,
        textAlign = TextAlign.Center,
        modifier = modifier
            .then(
                if (selected) Modifier.neveraShadow(
                    layers = NeveraTheme.shadow.small,
                    cornerRadius = NeveraTheme.radius.xLarge,
                ) else Modifier
            )
            .clip(RoundedCornerShape(NeveraTheme.radius.small))
            .background(if (selected) NeveraTheme.colors.surfacePrimary else NeveraTheme.colors.surfaceSecondary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(
                vertical = NeveraTheme.spacing.padding8,
                horizontal = NeveraTheme.spacing.padding12,
            ),
    )
}

private val StorageLocation.labelRes: Int
    get() = when (this) {
        StorageLocation.Fridge -> R.string.fridge_filter_refrigerator
        StorageLocation.Freezer -> R.string.fridge_filter_freezer
        StorageLocation.Pantry -> R.string.fridge_filter_room_temp
    }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeFilterTabRowPreview() {
    NeveraTheme {
        FridgeFilterTabRow(
            selectedFilter = StorageLocationFilter.All,
            onFilterSelected = {},
            modifier = Modifier.padding(NeveraTheme.spacing.padding16),
        )
    }
}
