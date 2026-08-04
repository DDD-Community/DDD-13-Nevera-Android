package com.anddd.nevera.feature.splash.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.splash.R

private const val TopSpacerWeight = 0.08f
private const val TitleWeight = 0.52f
private const val GapWeight = 0.05f
private const val IllustrationWeight = 0.28f
private const val BottomSpacerWeight = 0.07f
private const val LogoHeightFraction = 0.24f
private const val LogoAspectRatio = 224f / 100f

@Composable
internal fun SplashContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(TopSpacerWeight))

        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(TitleWeight)
                .padding(horizontal = NeveraTheme.spacing.padding16),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = NeveraIcons.Logo100,
                contentDescription = stringResource(R.string.splash_logo_description),
                modifier = Modifier
                    .fillMaxHeight(LogoHeightFraction)
                    .aspectRatio(LogoAspectRatio),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = NeveraTheme.typography.headlineSmall,
                color = NeveraTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(GapWeight))

        Image(
            painter = painterResource(R.drawable.img_splash),
            contentDescription = stringResource(R.string.splash_illustration_description),
            modifier = Modifier
                .weight(IllustrationWeight)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.weight(BottomSpacerWeight))
    }
}

@Preview(showBackground = true)
@Composable
fun SplashContentPreview() {
    NeveraTheme {
        SplashContent()
    }
}
