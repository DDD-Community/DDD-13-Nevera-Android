package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shadow.neveraShadow
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder

@Composable
internal fun FridgeSortOrderDropdownMenu(
    density: Density,
    selectedSortOrder: IngredientSortOrder,
    onSortOrderSelected: (IngredientSortOrder) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Popup(
        popupPositionProvider = remember(density) {
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset = with(density) {
                    val verticalGap = NeveraTheme.spacing.gap4.roundToPx()
                    val preferredX = anchorBounds.right - popupContentSize.width
                    val preferredY = anchorBounds.bottom + verticalGap

                    val clampedX = preferredX.coerceIn(
                        minimumValue = 0,
                        maximumValue = (windowSize.width - popupContentSize.width).coerceAtLeast(0),
                    )
                    val clampedY = preferredY.coerceIn(
                        minimumValue = 0,
                        maximumValue = (windowSize.height - popupContentSize.height).coerceAtLeast(0),
                    )

                    IntOffset(x = clampedX, y = clampedY)
                }
            }
        },
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(dismissOnClickOutside = true),
    ) {
        FridgeSortOrderDropdownMenuContent(
            selectedSortOrder = selectedSortOrder,
            onSortOrderSelected = onSortOrderSelected,
        )
    }
}

@Composable
private fun FridgeSortOrderDropdownMenuContent(
    selectedSortOrder: IngredientSortOrder,
    onSortOrderSelected: (IngredientSortOrder) -> Unit,
) {
    Surface(
        modifier = Modifier.neveraShadow(
            layers = NeveraTheme.shadow.medium,
            cornerRadius = NeveraTheme.radius.large,
        ),
        shape = RoundedCornerShape(NeveraTheme.radius.large),
        color = NeveraTheme.colors.surfacePrimary,
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(vertical = NeveraTheme.spacing.padding4),
        ) {
            IngredientSortOrder.entries.forEach { order ->
                FridgeSortOrderDropdownMenuItem(
                    order = order,
                    isSelected = order == selectedSortOrder,
                    onClick = { onSortOrderSelected(order) },
                )
            }
        }
    }
}

@Composable
private fun FridgeSortOrderDropdownMenuItem(
    order: IngredientSortOrder,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(
                horizontal = NeveraTheme.spacing.padding16,
                vertical = NeveraTheme.spacing.padding8,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap8, Alignment.End),
    ) {
        Text(
            text = stringResource(order.labelRes),
            style = NeveraTheme.typography.titleXSmall,
            color = if (isSelected) NeveraTheme.colors.textPrimary else NeveraTheme.colors.textTertiary,
        )
        Icon(
            painter = NeveraIcons.Check,
            contentDescription = null,
            tint = if (isSelected) NeveraTheme.colors.primaryNormal else NeveraTheme.colors.iconDisabled,
            modifier = Modifier.size(NeveraTheme.iconSize.small),
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
private fun FridgeSortOrderDropdownMenuContentPreview() {
    NeveraTheme {
        FridgeSortOrderDropdownMenuContent(
            selectedSortOrder = IngredientSortOrder.ExpiryDate,
            onSortOrderSelected = {},
        )
    }
}
