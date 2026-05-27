package com.anddd.nevera.feature.ingredient.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.domain.model.ingredient.StorageLocation

/** 화면 표시용 보관 방법 한국어명 (strings.xml 참조) */
@Composable
fun StorageLocation.displayName(): String = stringResource(
    when (this) {
        StorageLocation.Fridge -> R.string.ingredient_storage_location_fridge
        StorageLocation.Freezer -> R.string.ingredient_storage_location_freezer
        StorageLocation.Pantry -> R.string.ingredient_storage_location_pantry
    }
)
