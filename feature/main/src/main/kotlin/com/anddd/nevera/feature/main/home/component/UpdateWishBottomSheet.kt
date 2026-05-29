package com.anddd.nevera.feature.main.home.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.feature.main.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpdateWishBottomSheet(
    wishName: String,
    goalAmount: Long,
    onWishUpdated: (name: String, goalAmount: Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WishFormBottomSheet(
        initialName = wishName,
        initialAmount = goalAmount.toString(),
        confirmLabel = stringResource(R.string.home_update_wish_step2_cta),
        onWishSaved = onWishUpdated,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}
