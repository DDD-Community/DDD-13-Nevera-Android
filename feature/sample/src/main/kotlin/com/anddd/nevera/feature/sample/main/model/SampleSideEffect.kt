package com.anddd.nevera.feature.sample.main.model

sealed interface SampleSideEffect {
    data class ShowToast(val message: String) : SampleSideEffect
}
