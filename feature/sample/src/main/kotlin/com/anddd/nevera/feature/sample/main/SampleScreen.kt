package com.anddd.nevera.feature.sample.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.sample.main.component.SampleContent
import com.anddd.nevera.feature.sample.main.model.SampleIntent
import com.anddd.nevera.feature.sample.main.model.SampleSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SampleScreen(
    viewModel: SampleViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is SampleSideEffect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    SampleContent(
        count = state.count,
        onButtonClick = { viewModel.handleIntent(SampleIntent.ClickButton) },
    )
}
