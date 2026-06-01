package com.anddd.nevera.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.domain.model.ingredient.FoodCategory

@DrawableRes
fun FoodCategory.iconRes(): Int = when (this) {
    FoodCategory.Veg -> R.drawable.img_listcell_vegetable
    FoodCategory.Fruit -> R.drawable.img_listcell_fruit
    FoodCategory.MeatEggs -> R.drawable.img_listcell_meat
    FoodCategory.Sea -> R.drawable.img_listcell_seafood
    FoodCategory.Dairy -> R.drawable.img_listcell_dairy
    FoodCategory.Sauce -> R.drawable.img_listcell_sauce
    FoodCategory.Drink -> R.drawable.img_listcell_beverage
    FoodCategory.Processed -> R.drawable.img_listcell_processed
    FoodCategory.Etc -> R.drawable.img_listcell_etc
}

@Composable
fun FoodCategory.displayName(): String = stringResource(
    when (this) {
        FoodCategory.Veg -> R.string.ingredient_category_vegetable
        FoodCategory.Fruit -> R.string.ingredient_category_fruit
        FoodCategory.MeatEggs -> R.string.ingredient_category_meat_egg
        FoodCategory.Sea -> R.string.ingredient_category_seafood
        FoodCategory.Dairy -> R.string.ingredient_category_dairy
        FoodCategory.Sauce -> R.string.ingredient_category_sauce
        FoodCategory.Drink -> R.string.ingredient_category_beverage
        FoodCategory.Processed -> R.string.ingredient_category_processed
        FoodCategory.Etc -> R.string.ingredient_category_other
    },
)
