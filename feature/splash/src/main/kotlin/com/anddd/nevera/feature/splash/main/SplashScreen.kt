package com.anddd.nevera.feature.splash.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.splash.R
import com.anddd.nevera.feature.splash.main.model.SplashSideEffect

private val LogoWidth = 222.dp
private val LogoHeight = 100.dp

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    NotificationPermissionRequester(
        onPermissionFlowCompleted = viewModel::startAutoLogin,
    )

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SplashSideEffect.MoveToHome -> onNavigateToHome(effect.accessToken)
                is SplashSideEffect.MoveToLogin -> onNavigateToLogin()
            }
        }
    }

    SplashContent()
}

@Composable
fun SplashContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.padding8))

        Image(
            painter = ColorPainter(NeveraTheme.colors.primaryNormal),
            contentDescription = stringResource(R.string.splash_logo_description),
            modifier = Modifier.size(
                width = LogoWidth,
                height = LogoHeight,
            ),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.padding8))

        Text(
            text = stringResource(R.string.splash_subtitle),
            style = NeveraTheme.typography.headlineSmall,
            color = NeveraTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashContentPreview() {
    NeveraTheme {
        SplashContent()
    }
}
