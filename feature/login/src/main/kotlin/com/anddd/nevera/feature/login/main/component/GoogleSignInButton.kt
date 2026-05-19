package com.anddd.nevera.feature.login.main.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.login.R

@Composable
internal fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
            .requiredHeight(48.dp),
        shape = RoundedCornerShape(NeveraTheme.radius.small),
        color = NeveraTheme.colors.surfacePrimary,
        border = BorderStroke(1.dp, NeveraTheme.colors.borderStrong),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.img_google),
                contentDescription = null,
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
            )
            Spacer(Modifier.width(NeveraTheme.spacing.gap8))
            Text(
                text = stringResource(R.string.login_google_button),
                style = NeveraTheme.typography.titleMedium,
                color = NeveraTheme.colors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleSignInButtonPreview() {
    NeveraTheme {
        GoogleSignInButton(onClick = {})
    }
}