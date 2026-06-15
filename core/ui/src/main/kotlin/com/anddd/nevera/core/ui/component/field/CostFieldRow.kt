package com.anddd.nevera.core.ui.component.field

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R

@Composable
fun CostFieldRow(
    cost: Int,
    onCostChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldRowLabel(stringResource(R.string.field_label_cost))
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        NeveraTextField(
            value = cost.takeIf { it > 0 }?.toString() ?: "0",
            onValueChange = { input ->
                val filtered = input.filter { it in '0'..'9' }.take(9)
                val newCost = filtered.toIntOrNull() ?: 0
                onCostChanged(newCost)
            },
            modifier = Modifier.weight(1f),
            useIcon = false,
            suffix = { NeveraTextFieldSuffix(stringResource(R.string.field_cost_unit)) },
            config = NeveraTextFieldConfig(
                placeholder = "0",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            ),
        )
    }
}
