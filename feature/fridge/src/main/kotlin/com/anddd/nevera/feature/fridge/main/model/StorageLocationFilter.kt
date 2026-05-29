package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.domain.model.ingredient.StorageLocation

sealed interface StorageLocationFilter {
    data object All : StorageLocationFilter
    data class Specific(val location: StorageLocation) : StorageLocationFilter
}
