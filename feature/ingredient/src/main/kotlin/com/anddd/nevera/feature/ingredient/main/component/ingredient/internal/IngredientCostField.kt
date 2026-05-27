package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

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
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 금액 필드 행
 *
 * 레이블(금액) + 숫자 전용 [NeveraTextField] + "원" suffix로 구성됩니다.
 * 0 입력 또는 빈 값은 0으로 처리됩니다.
 */
@Composable
internal fun IngredientCostField(
    cost: Int,
    onCostChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_cost)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        NeveraTextField(
            value = cost.takeIf { it > 0 }?.toString() ?: "0",
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }.take(9) // Int 오버플로우 방지
                val newCost = filtered.toIntOrNull() ?: 0
                onCostChanged(newCost)
            },
            modifier = Modifier.weight(1f),
            useIcon = false,
            suffix = { NeveraTextFieldSuffix(stringResource(R.string.ingredient_item_cost_unit)) },
            config = NeveraTextFieldConfig(
                placeholder = "0",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            ),
        )
    }
}

@Preview(
    name = "IngredientCostField - 금액 없음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientCostFieldEmptyPreview() {
    NeveraTheme {
        IngredientCostField(
            cost = 0,
            onCostChanged = {},
        )
    }
}

@Preview(
    name = "IngredientCostField - 금액 있음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientCostFieldPreview() {
    NeveraTheme {
        IngredientCostField(
            cost = 4800,
            onCostChanged = {},
        )
    }
}
