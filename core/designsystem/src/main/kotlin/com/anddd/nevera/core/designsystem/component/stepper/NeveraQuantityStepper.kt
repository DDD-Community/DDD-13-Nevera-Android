package com.anddd.nevera.core.designsystem.component.stepper

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.annotation.DrawableRes
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

private object StepperDimension {
    val Height = 36.dp
    val ButtonSize = 28.dp
    val CounterMinWidth = 18.dp
}

private object StepperDefaults {
    const val MIN_QUANTITY = 1
    const val MAX_QUANTITY = 999
}

/**
 * 수량 조절 Stepper (Stateless)
 * 상태 관리는 호출 측에서 담당
 *
 * @param quantity    현재 수량 — 호출 측에서 [minQuantity]..[maxQuantity] 범위를 보장해야 함.
 *                    범위를 벗어난 값이 전달되면 해당 방향 버튼이 비활성화되어 콜백이 호출되지 않음.
 * @param onDecrease  감소 버튼 탭 콜백 — [quantity] > [minQuantity] 일 때만 호출됨
 * @param onIncrease  증가 버튼 탭 콜백 — [quantity] < [maxQuantity] 일 때만 호출됨
 * @param modifier    외부 Modifier
 * @param minQuantity 최솟값 (기본 1) — 이 값 이하면 감소 버튼 비활성
 * @param maxQuantity 최댓값 (기본 999) — 이 값 이상이면 증가 버튼 비활성
 */
@Composable
fun NeveraQuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = StepperDefaults.MIN_QUANTITY,
    maxQuantity: Int = StepperDefaults.MAX_QUANTITY,
) {
    val canDecrease = quantity > minQuantity
    val canIncrease = quantity < maxQuantity

    Row(
        modifier = modifier.height(StepperDimension.Height)
            .border(
                width = 1.dp,
                color = NeveraTheme.colors.borderNormal,
                shape = RoundedCornerShape(NeveraTheme.radius.small),
            )
            .padding(horizontal = NeveraTheme.spacing.padding4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperIconButton(
            onClick = onDecrease,
            enabled = canDecrease,
            iconRes = R.drawable.ic_stepper_minus_active,
            contentDescription = stringResource(R.string.nevera_stepper_decrease),
        )

        Text(
            text = quantity.toString(),
            modifier = Modifier.widthIn(min = StepperDimension.CounterMinWidth),
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )

        StepperIconButton(
            onClick = onIncrease,
            enabled = canIncrease,
            iconRes = R.drawable.ic_stepper_plus_active,
            contentDescription = stringResource(R.string.nevera_stepper_increase),
        )
    }
}

@Composable
private fun StepperIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    @DrawableRes iconRes: Int,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(StepperDimension.ButtonSize),
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = if (enabled) NeveraTheme.colors.iconPrimary
            else NeveraTheme.colors.iconDisabled,
            modifier = Modifier.size(NeveraTheme.iconSize.xxSmall),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NeveraQuantityStepperPreview() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12),
            modifier = Modifier.padding(NeveraTheme.spacing.padding16),
        ) {
            // 최솟값 — 감소 버튼 비활성
            NeveraQuantityStepper(quantity = 1, onDecrease = {}, onIncrease = {})
            // 일반
            NeveraQuantityStepper(quantity = 5, onDecrease = {}, onIncrease = {})
            // 최댓값 — 증가 버튼 비활성
            NeveraQuantityStepper(quantity = 999, onDecrease = {}, onIncrease = {})
        }
    }
}
