package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraIllustrationBottomSheet
import com.anddd.nevera.feature.main.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GreetingBottomSheet(
    onCreateWishClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    NeveraIllustrationBottomSheet(
        sheetState = sheetState,
        illustration = {
            Image(
                painter = painterResource(R.drawable.img_greeting),
                contentDescription = null,
            )
        },
        title = stringResource(R.string.home_greeting_title),
        subtitle = stringResource(R.string.home_greeting_subtitle),
        primaryLabel = stringResource(R.string.home_greeting_primary),
        onPrimaryClick = onCreateWishClick,
        onDismissRequest = onSkipClick,
        modifier = modifier,
        ghostLabel = stringResource(R.string.home_greeting_skip),
        onGhostClick = onSkipClick,
    )
}
