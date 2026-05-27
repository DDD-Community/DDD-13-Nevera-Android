package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R

private val DividerWidth = 1.dp
private val DividerHeight = 24.dp

@Composable
fun RescueDisposalCostCard(
    rescueAmount: Int,
    disposalAmount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(NeveraTheme.colors.surfacePrimary)
            .padding(vertical = NeveraTheme.spacing.padding16),
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CostColumn(
            label = stringResource(R.string.home_cost_rescue_label),
            amount = rescueAmount,
            modifier = Modifier.weight(1f),
        )
        VerticalDivider(
            thickness = DividerWidth,
            color = NeveraTheme.colors.borderNormal,
            modifier = Modifier.height(DividerHeight),
        )
        CostColumn(
            label = stringResource(R.string.home_cost_disposal_label),
            amount = disposalAmount,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CostColumn(
    label: String,
    amount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(NeveraTheme.spacing.padding6),
    ) {
        Text(
            text = label,
            style = NeveraTheme.typography.captionLarge,
            color = NeveraTheme.colors.textQuaternary,
        )
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap6))
        Text(
            text = "%,d원".format(amount),
            style = NeveraTheme.typography.titleLarge,
            color = NeveraTheme.colors.textTertiary,
        )
    }
}

@Preview(
    name = "RescueDisposalCostCard - Zero",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun RescueDisposalCostCardPreview() {
    NeveraTheme {
        RescueDisposalCostCard(
            rescueAmount = 0,
            disposalAmount = 0,
        )
    }
}

@Preview(
    name = "RescueDisposalCostCard - With Amount",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun RescueDisposalCostCardWithAmountPreview() {
    NeveraTheme {
        RescueDisposalCostCard(
            rescueAmount = 12500,
            disposalAmount = 3200,
        )
    }
}
