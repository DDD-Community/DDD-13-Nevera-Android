package com.anddd.nevera.feature.login.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.login.R

@Composable
internal fun LoginEtcSection(
    onGoogleLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f)
                    .padding(end = NeveraTheme.spacing.padding12),
                thickness = 1.dp,
                color = NeveraTheme.colors.borderNormal,
            )
            Text(
                text = stringResource(R.string.login_divider_or),
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textCaption,
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f)
                    .padding(start = NeveraTheme.spacing.padding12),
                thickness = 1.dp,
                color = NeveraTheme.colors.borderNormal,
            )
        }
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        GoogleSignInButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        Row(
            modifier = Modifier.height(34.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.login_signup_guide),
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textQuaternary,
            )
            Spacer(Modifier.width(NeveraTheme.spacing.padding10))
            Text(
                text = stringResource(R.string.login_signup_button),
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.primaryNormal,
                modifier = Modifier.clickable { onSignupClick() }
                    .padding(horizontal = NeveraTheme.spacing.padding6),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginEtcSectionPreview() {
    NeveraTheme {
        LoginEtcSection(
            onGoogleLoginClick = {},
            onSignupClick = {},
        )
    }
}
