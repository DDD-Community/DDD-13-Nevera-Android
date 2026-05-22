package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anddd.nevera.core.designsystem.component.stepper.NeveraQuantityStepper
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 수량 필드 행
 *
 * 레이블(수량) + [NeveraQuantityStepper]로 구성됩니다.
 * 최솟값 1 미만으로 감소하지 않습니다.
 */
@Composable
internal fun IngredientQuantityField(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(IngredientItemCardDimension.QuantityRowHeight)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_quantity)
        Spacer(modifier = Modifier.weight(1f))
        NeveraQuantityStepper(
            quantity = quantity,
            onDecrease = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
            onIncrease = { onQuantityChanged(quantity + 1) },
        )
    }
}
