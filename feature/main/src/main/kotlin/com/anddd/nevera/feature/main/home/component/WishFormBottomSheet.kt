package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraStepContentBottomSheet
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.feature.main.R

private fun wishNameFieldState(value: String): NeveraTextFieldState = when {
    value.isEmpty() -> NeveraTextFieldState.Normal
    value.length <= 15 -> NeveraTextFieldState.Positive
    else -> NeveraTextFieldState.Negative
}

private fun goalAmountFieldState(value: String): NeveraTextFieldState = when {
    value.isEmpty() -> NeveraTextFieldState.Normal
    (value.toLongOrNull() ?: 0L) > 0L && value.length <= 10 -> NeveraTextFieldState.Positive
    else -> NeveraTextFieldState.Negative
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WishFormBottomSheet(
    initialName: String,
    initialAmount: String,
    confirmLabel: String,
    onWishSaved: (name: String, goalAmount: Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableIntStateOf(1) }
    var wishName by remember { mutableStateOf(initialName) }
    var goalAmount by remember { mutableStateOf(initialAmount) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val wishNameState = wishNameFieldState(wishName)
    val goalAmountState = goalAmountFieldState(goalAmount)
    val isCurrentStepPositive = if (step == 1) {
        wishNameState == NeveraTextFieldState.Positive
    } else {
        goalAmountState == NeveraTextFieldState.Positive
    }

    NeveraStepContentBottomSheet(
        sheetState = sheetState,
        stepIndicator = if (step == 1) {
            stringResource(R.string.home_create_wish_step1_indicator)
        } else {
            stringResource(R.string.home_create_wish_step2_indicator)
        },
        title = if (step == 1) {
            stringResource(R.string.home_create_wish_step1_title)
        } else {
            stringResource(R.string.home_create_wish_step2_title)
        },
        subtitle = if (step == 1) {
            stringResource(R.string.home_create_wish_step1_subtitle)
        } else {
            stringResource(R.string.home_create_wish_step2_subtitle)
        },
        backLabel = if (step == 1) null else stringResource(R.string.home_create_wish_back),
        ctaLabel = if (step == 1) {
            stringResource(R.string.home_create_wish_step1_cta)
        } else {
            confirmLabel
        },
        ctaEnabled = isCurrentStepPositive,
        onBackClick = { step = 1 },
        onCtaClick = {
            if (step == 1) {
                step = 2
            } else {
                val amount = goalAmount.toLongOrNull() ?: return@NeveraStepContentBottomSheet
                onWishSaved(wishName, amount)
            }
        },
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        if (step == 1) {
            NeveraTextField(
                value = wishName,
                onValueChange = { wishName = it },
                modifier = Modifier.fillMaxWidth(),
                useIcon = true,
                config = NeveraTextFieldConfig(
                    type = NeveraTextFieldType.Underline,
                    state = wishNameState,
                    placeholder = stringResource(R.string.home_create_wish_step1_placeholder),
                    description = stringResource(R.string.home_create_wish_step1_description),
                    singleLine = true,
                ),
            )
        } else {
            NeveraTextField(
                value = goalAmount,
                onValueChange = { goalAmount = it },
                modifier = Modifier.fillMaxWidth(),
                useIcon = true,
                suffix = { NeveraTextFieldSuffix(stringResource(R.string.home_create_wish_step2_suffix)) },
                config = NeveraTextFieldConfig(
                    type = NeveraTextFieldType.Underline,
                    state = goalAmountState,
                    placeholder = stringResource(R.string.home_create_wish_step2_placeholder),
                    description = stringResource(R.string.home_create_wish_step2_description),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ),
            )
        }
    }
}
