package com.anddd.nevera.feature.sample.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface SimpleMutation : NeveraMutation {
    data object IncrementCount : SimpleMutation
}
