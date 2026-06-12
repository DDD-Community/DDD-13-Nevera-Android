package com.anddd.nevera.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.domain.model.ingredient.StorageLocation

@Composable
fun StorageLocation.displayName(): String = stringResource(
    when (this) {
        StorageLocation.Fridge -> R.string.ingredient_storage_location_fridge
        StorageLocation.Freezer -> R.string.ingredient_storage_location_freezer
        StorageLocation.Pantry -> R.string.ingredient_storage_location_pantry
    }
)
