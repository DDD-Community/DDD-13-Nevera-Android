package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.IngredientSortOrder
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter

@Composable
internal fun FridgeIngredientListHeader(
    totalCount: Int,
    selectedStorageFilter: StorageLocationFilter,
    selectedSortOrder: IngredientSortOrder,
    onSortOrderSelected: (IngredientSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = NeveraTheme.spacing.padding16,
                vertical = NeveraTheme.spacing.padding8,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${stringResource(selectedStorageFilter.labelRes)} $totalCount",
            style = NeveraTheme.typography.captionLarge,
            color = NeveraTheme.colors.textCaption,
            modifier = Modifier.weight(1f),
        )
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { dropdownExpanded = true },
                ),
            ) {
                Text(
                    text = stringResource(selectedSortOrder.labelRes),
                    style = NeveraTheme.typography.captionLarge,
                    color = NeveraTheme.colors.textCaption,
                )
                Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap4))
                Icon(
                    painter = NeveraIcons.ChevronSmallDown,
                    contentDescription = null,
                    tint = NeveraTheme.colors.iconCaption,
                    modifier = Modifier.size(NeveraTheme.iconSize.small),
                )
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
            ) {
                IngredientSortOrder.entries.forEach { order ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(order.labelRes),
                                style = NeveraTheme.typography.bodyMedium,
                                color = NeveraTheme.colors.textSecondary,
                            )
                        },
                        trailingIcon = {
                            if (order == selectedSortOrder) {
                                Icon(
                                    painter = NeveraIcons.Check,
                                    contentDescription = null,
                                    tint = NeveraTheme.colors.iconPrimary,
                                    modifier = Modifier.size(NeveraTheme.iconSize.small),
                                )
                            }
                        },
                        onClick = {
                            onSortOrderSelected(order)
                            dropdownExpanded = false
                        },
                    )
                }
            }
        }
    }
}

private val StorageLocationFilter.labelRes: Int
    get() = when (this) {
        StorageLocationFilter.All -> R.string.fridge_filter_all
        is StorageLocationFilter.Specific -> when (this.location) {
            StorageLocation.Fridge -> R.string.fridge_filter_refrigerator
            StorageLocation.Freezer -> R.string.fridge_filter_freezer
            StorageLocation.Pantry -> R.string.fridge_filter_room_temp
        }
    }

private val IngredientSortOrder.labelRes: Int
    get() = when (this) {
        IngredientSortOrder.ExpiryDate -> R.string.fridge_list_sort_expiry
        IngredientSortOrder.Latest -> R.string.fridge_list_sort_latest
    }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientListHeaderPreview() {
    NeveraTheme {
        FridgeIngredientListHeader(
            totalCount = 0,
            selectedStorageFilter = StorageLocationFilter.All,
            selectedSortOrder = IngredientSortOrder.ExpiryDate,
            onSortOrderSelected = {},
        )
    }
}
