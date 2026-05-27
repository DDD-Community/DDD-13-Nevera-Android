package com.anddd.nevera.feature.main.home.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anddd.nevera.feature.main.R

data class IngredientFilterTabUiModel(
    val type: IngredientFilterTab,
    @StringRes val labelRes: Int,
    @DrawableRes val selectedIconRes: Int,
    @DrawableRes val unselectedIconRes: Int,
)

enum class IngredientFilterTab {
    Rescue,
    Disposal,
}

internal fun List<IngredientFilterTab>.toUiModel(): List<IngredientFilterTabUiModel> {
    return this.map { tab ->
        when (tab) {
            IngredientFilterTab.Rescue -> {
                IngredientFilterTabUiModel(
                    type = IngredientFilterTab.Rescue,
                    labelRes = R.string.home_ingredient_tab_rescue,
                    selectedIconRes = R.drawable.ic_ingredient_rescue_active,
                    unselectedIconRes = R.drawable.ic_ingredient_rescue_inactive
                )
            }

            IngredientFilterTab.Disposal -> {
                IngredientFilterTabUiModel(
                    type = IngredientFilterTab.Disposal,
                    labelRes = R.string.home_ingredient_tab_disposal,
                    selectedIconRes = R.drawable.ic_ingredient_disposal_active,
                    unselectedIconRes = R.drawable.ic_ingredient_disposal_inactive
                )
            }
        }
    }
}
