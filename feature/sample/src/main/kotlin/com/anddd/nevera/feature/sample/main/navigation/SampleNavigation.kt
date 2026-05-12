package com.anddd.nevera.feature.sample.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.sample.main.SampleScreen

const val SAMPLE_ROUTE = "sample"

fun NavGraphBuilder.sampleScreen() {
    composable(route = SAMPLE_ROUTE) {
        SampleScreen()
    }
}
