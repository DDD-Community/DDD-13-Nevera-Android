package com.anddd.nevera.core.ui.component.field

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.component.stepper.NeveraQuantityStepper
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R

@Composable
fun QuantityFieldRow(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(FieldRowDimension.QuantityRowHeight)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldRowLabel(stringResource(R.string.field_label_quantity))
        Spacer(modifier = Modifier.weight(1f))
        NeveraQuantityStepper(
            quantity = quantity,
            onDecrease = { onQuantityChanged((quantity - 1).coerceAtLeast(0)) },
            onIncrease = { onQuantityChanged(quantity + 1) },
        )
    }
}
