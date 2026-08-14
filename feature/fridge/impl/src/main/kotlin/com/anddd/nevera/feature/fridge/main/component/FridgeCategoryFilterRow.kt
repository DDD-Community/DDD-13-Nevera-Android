package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.CategoryFilter

@Composable
internal fun FridgeCategoryFilterRow(
    selectedFilter: CategoryFilter,
    onFilterSelected: (CategoryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap8),
        contentPadding = PaddingValues(horizontal = NeveraTheme.spacing.padding16),
    ) {
        item {
            FridgeCategoryChip(
                label = stringResource(R.string.fridge_category_all),
                selected = selectedFilter is CategoryFilter.All,
                onClick = { onFilterSelected(CategoryFilter.All) },
            )
        }
        items(FoodCategory.entries) { category ->
            FridgeCategoryChip(
                label = stringResource(category.labelRes),
                selected = selectedFilter is CategoryFilter.Specific &&
                        selectedFilter.category == category,
                onClick = { onFilterSelected(CategoryFilter.Specific(category)) },
            )
        }
    }
}

@Composable
private fun FridgeCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(NeveraTheme.radius.max)
    if (selected) {
        NeveraFilledButton(
            label = label,
            onClick = onClick,
            color = NeveraButtonColor.Secondary,
            size = NeveraButtonSize.Small,
            shape = shape,
            modifier = modifier,
        )
    } else {
        NeveraOutlinedButton(
            label = label,
            onClick = onClick,
            color = NeveraButtonColor.Secondary,
            size = NeveraButtonSize.Small,
            shape = shape,
            modifier = modifier,
        )
    }
}

private val FoodCategory.labelRes: Int
    get() = when (this) {
        FoodCategory.Veg -> R.string.fridge_category_vegetable
        FoodCategory.Fruit -> R.string.fridge_category_fruit
        FoodCategory.MeatEggs -> R.string.fridge_category_meat_egg
        FoodCategory.Sea -> R.string.fridge_category_seafood
        FoodCategory.Dairy -> R.string.fridge_category_dairy
        FoodCategory.Sauce -> R.string.fridge_category_sauce
        FoodCategory.Drink -> R.string.fridge_category_beverage
        FoodCategory.Processed -> R.string.fridge_category_processed
        FoodCategory.Etc -> R.string.fridge_category_other
    }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeCategoryFilterRowPreview() {
    NeveraTheme {
        FridgeCategoryFilterRow(
            selectedFilter = CategoryFilter.All,
            onFilterSelected = {},
            modifier = Modifier.padding(vertical = NeveraTheme.spacing.padding8),
        )
    }
}
