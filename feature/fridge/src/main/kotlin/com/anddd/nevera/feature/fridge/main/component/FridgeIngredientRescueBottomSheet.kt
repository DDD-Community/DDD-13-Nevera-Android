package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraActionBottomSheet
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import java.time.LocalDate
import kotlin.math.roundToInt

private const val RatioMin = 0.25f
private const val RatioHalf = 0.50f
private const val RatioThreeQuarters = 0.75f
private const val RatioMax = 1.0f
private val RatioValueRange = 0f..RatioMax
private const val RatioSteps = 3

private val ThumbSize = 24.dp
private val ThumbElevation = 2.dp
private val TrackHeight = 8.dp
private val TrackDotRadius = 2.dp
private val TrackDotFractions = listOf(0.25f, 0.5f, 0.75f, 0.99f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FridgeIngredientRescueBottomSheet(
    item: FridgeIngredientUiModel,
    sheetState: SheetState,
    onConfirmClick: (ratio: Float) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var ratio by remember { mutableFloatStateOf(RatioMax) }

    NeveraActionBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.fridge_rescue_sheet_question),
        confirmLabel = stringResource(R.string.fridge_rescue_sheet_confirm),
        onConfirm = { onConfirmClick(ratio) },
        onDismissRequest = onDismissRequest,
    ) {
        RescueBottomSheetContent(
            item = item,
            ratio = ratio,
            onRatioChange = { ratio = it },
        )
    }
}

@Composable
private fun RescueBottomSheetContent(
    item: FridgeIngredientUiModel,
    ratio: Float,
    onRatioChange: (Float) -> Unit,
) {
    val rescueAmount = (item.cost * ratio).roundToInt()

    HorizontalDivider(color = NeveraTheme.colors.dividerNormal)

    Column(
        modifier = Modifier.padding(
            horizontal = NeveraTheme.spacing.padding20,
            vertical = NeveraTheme.spacing.padding16,
        ),
    ) {
        Text(
            text = percentageLabel(ratio),
            style = NeveraTheme.typography.headlineMedium,
            color = NeveraTheme.colors.primaryNormal,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        RescueSlider(
            ratio = ratio,
            onRatioChange = onRatioChange,
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

        HorizontalDivider(color = NeveraTheme.colors.dividerNormal)

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

        RescuePriceRow(
            label = stringResource(R.string.fridge_rescue_sheet_rescue_amount_label),
            amount = stringResource(R.string.fridge_rescue_sheet_rescue_amount_format, rescueAmount),
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap6))

        TotalPriceRow(
            label = stringResource(R.string.fridge_rescue_sheet_total_amount_label),
            amount = stringResource(R.string.fridge_rescue_sheet_total_amount_format, item.cost),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RescueSlider(
    ratio: Float,
    onRatioChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Slider(
            value = ratio,
            onValueChange = { onRatioChange(maxOf(it, RatioMin)) },
            modifier = modifier.fillMaxWidth(),
            valueRange = RatioValueRange,
            steps = RatioSteps,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(ThumbSize)
                        .shadow(elevation = ThumbElevation, shape = CircleShape)
                        .background(Color.White, CircleShape),
                )
            },
            track = { sliderState ->
                val activeColor = NeveraTheme.colors.primaryNormal
                val inactiveColor = NeveraTheme.colors.surfaceSecondary
                val dotColor = NeveraTheme.colors.surfaceQuaternary
                val fraction = sliderState.value

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TrackHeight),
                ) {
                    val trackH = size.height
                    val trackW = size.width
                    val cr = CornerRadius(trackH / 2)
                    val dotRadius = TrackDotRadius.toPx()
                    val activeWidth = trackW * fraction

                    drawRoundRect(
                        color = inactiveColor,
                        size = Size(trackW, trackH),
                        cornerRadius = cr,
                    )

                    TrackDotFractions.forEach { stepFraction ->
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(trackW * stepFraction, trackH / 2),
                        )
                    }

                    drawRoundRect(
                        color = activeColor,
                        size = Size(activeWidth, trackH),
                        cornerRadius = cr,
                    )
                }
            },
        )
        Text(
            text = "${(ratio * 100).roundToInt()}%",
            style = NeveraTheme.typography.captionMedium,
            color = NeveraTheme.colors.textCaption,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RescuePriceRow(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.textTertiary,
        )
        Text(
            text = amount,
            style = NeveraTheme.typography.headlineSmall,
            color = NeveraTheme.colors.textTertiary,
        )
    }
}

@Composable
private fun TotalPriceRow(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.textCaption,
        )
        Text(
            text = amount,
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.textCaption,
        )
    }
}

@Composable
private fun percentageLabel(ratio: Float): String = when {
    ratio <= RatioMin -> stringResource(R.string.fridge_rescue_percentage_little)
    ratio <= RatioHalf -> stringResource(R.string.fridge_rescue_percentage_half)
    ratio <= RatioThreeQuarters -> stringResource(R.string.fridge_rescue_percentage_more)
    else -> stringResource(R.string.fridge_rescue_percentage_all)
}

private val previewItem = FridgeIngredientUiModel(
    id = 1L,
    name = "국내산 백오이",
    category = FoodCategory.Veg,
    quantity = 1,
    cost = 24990,
    expiryDate = LocalDate.now().plusDays(2),
)

@Preview(name = "RescueBottomSheet - 25% (조금 먹었어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientRescueBottomSheetLittlePreview() {
    NeveraTheme {
        RescueBottomSheetContent(item = previewItem, ratio = RatioMin, onRatioChange = {})
    }
}

@Preview(name = "RescueBottomSheet - 50% (절반 정도 먹었어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientRescueBottomSheetContentPreview() {
    NeveraTheme {
        RescueBottomSheetContent(item = previewItem, ratio = RatioHalf, onRatioChange = {})
    }
}

@Preview(name = "RescueBottomSheet - 75% (절반보다 더 먹었어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientRescueBottomSheetMorePreview() {
    NeveraTheme {
        RescueBottomSheetContent(item = previewItem, ratio = RatioThreeQuarters, onRatioChange = {})
    }
}

@Preview(name = "RescueBottomSheet - 100% (다 먹었어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientRescueBottomSheetAllPreview() {
    NeveraTheme {
        RescueBottomSheetContent(item = previewItem, ratio = RatioMax, onRatioChange = {})
    }
}
