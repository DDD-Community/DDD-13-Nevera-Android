package com.anddd.nevera.feature.sample.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.sample.main.SampleScreen
import kotlinx.serialization.Serializable

@Serializable
data object SampleRoute

fun NavGraphBuilder.sampleScreen() {
    composable<SampleRoute> {
        SampleScreen()
    }
}
