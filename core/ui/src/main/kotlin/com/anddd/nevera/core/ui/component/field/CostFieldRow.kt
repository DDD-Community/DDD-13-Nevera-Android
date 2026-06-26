package com.anddd.nevera.core.ui.component.field

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CostFieldRow(
    cost: Int,
    onCostChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val imeVisible = WindowInsets.isImeVisible

    val formatCost: (Int) -> String = { value ->
        if (value == 0) "" else NumberFormat.getNumberInstance(Locale.KOREA).format(value)
    }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = formatCost(cost)))
    }

    LaunchedEffect(cost) {
        val formatted = formatCost(cost)
        if (formatted != textFieldValue.text) {
            textFieldValue = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
        }
    }

    // 키보드 스와이프 다운 시 포커스 해제
    LaunchedEffect(imeVisible) {
        if (!imeVisible) focusManager.clearFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldRowLabel(stringResource(R.string.field_label_cost))
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        NeveraTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val rawDigits = newValue.text.filter { it.isDigit() }.take(9)
                val newCost = rawDigits.toIntOrNull() ?: 0
                val formatted = formatCost(newCost)
                val digitsBeforeCursor = newValue.text.take(newValue.selection.start).count { it.isDigit() }
                val newCursorPos = adjustCursorToFormatted(digitsBeforeCursor, formatted)
                textFieldValue = TextFieldValue(text = formatted, selection = TextRange(newCursorPos))
                onCostChanged(newCost)
            },
            modifier = Modifier.weight(1f),
            useIcon = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            suffix = { NeveraTextFieldSuffix(stringResource(R.string.field_cost_unit)) },
            config = NeveraTextFieldConfig(
                placeholder = "0",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            ),
        )
    }
}

private fun adjustCursorToFormatted(digitsBeforeCursor: Int, formatted: String): Int {
    var digitCount = 0
    for (i in formatted.indices) {
        if (digitCount == digitsBeforeCursor) return i
        if (formatted[i].isDigit()) digitCount++
    }
    return formatted.length
}
