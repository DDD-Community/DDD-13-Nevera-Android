package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface HomeIntent : NeveraIntent {
    data class RecentIngredientTabClick(val tab: IngredientFilterTab) : HomeIntent
    data object AddIngredientClick : HomeIntent
}
