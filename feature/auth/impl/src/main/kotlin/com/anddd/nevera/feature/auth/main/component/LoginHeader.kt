package com.anddd.nevera.feature.auth.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.auth.R

@Composable
internal fun LoginHeader() {
    Column(
        modifier = Modifier.padding(top = NeveraTheme.spacing.gap20),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = NeveraIcons.Logo100,
            contentDescription = stringResource(R.string.login_logo_description),
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(72.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap6))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = NeveraTheme.typography.titleMedium,
            color = NeveraTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = NeveraTheme.spacing.gap20),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginHeaderPreview() {
    NeveraTheme {
        LoginHeader()
    }
}
