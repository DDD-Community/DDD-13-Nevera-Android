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

private const val DisposeRatioMin = 0.25f
private const val DisposeRatioHalf = 0.50f
private const val DisposeRatioThreeQuarters = 0.75f
private const val DisposeRatioMax = 1.0f
private val DisposeRatioValueRange = 0f..DisposeRatioMax
private const val DisposeRatioSteps = 3

private val DisposeThumbSize = 24.dp
private val DisposeThumbElevation = 2.dp
private val DisposeTrackHeight = 8.dp
private val DisposeTrackDotRadius = 2.dp
private val DisposeTrackDotFractions = listOf(0.25f, 0.5f, 0.75f, 0.99f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FridgeIngredientDisposeBottomSheet(
    item: FridgeIngredientUiModel,
    sheetState: SheetState,
    onConfirmClick: (ratio: Float) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var ratio by remember { mutableFloatStateOf(DisposeRatioMax) }

    NeveraActionBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.fridge_dispose_sheet_question),
        confirmLabel = stringResource(R.string.fridge_dispose_sheet_confirm),
        onConfirm = { onConfirmClick(ratio) },
        onDismissRequest = onDismissRequest,
    ) {
        DisposeBottomSheetContent(
            item = item,
            ratio = ratio,
            onRatioChange = { ratio = it },
        )
    }
}

@Composable
private fun DisposeBottomSheetContent(
    item: FridgeIngredientUiModel,
    ratio: Float,
    onRatioChange: (Float) -> Unit,
) {
    val disposeAmount = (item.cost * ratio).roundToInt()

    HorizontalDivider(color = NeveraTheme.colors.dividerNormal)

    Column(
        modifier = Modifier.padding(
            horizontal = NeveraTheme.spacing.padding20,
            vertical = NeveraTheme.spacing.padding16,
        ),
    ) {
        Text(
            text = disposePercentageLabel(ratio),
            style = NeveraTheme.typography.headlineMedium,
            color = NeveraTheme.colors.primaryNormal,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        DisposeSlider(
            ratio = ratio,
            onRatioChange = onRatioChange,
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

        HorizontalDivider(color = NeveraTheme.colors.dividerNormal)

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

        DisposePriceRow(
            label = stringResource(R.string.fridge_dispose_sheet_dispose_amount_label),
            amount = stringResource(R.string.fridge_dispose_sheet_dispose_amount_format, disposeAmount),
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap6))

        DisposeTotalPriceRow(
            label = stringResource(R.string.fridge_dispose_sheet_total_amount_label),
            amount = stringResource(R.string.fridge_dispose_sheet_total_amount_format, item.cost),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisposeSlider(
    ratio: Float,
    onRatioChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Slider(
            value = ratio,
            onValueChange = { onRatioChange(maxOf(it, DisposeRatioMin)) },
            modifier = modifier.fillMaxWidth(),
            valueRange = DisposeRatioValueRange,
            steps = DisposeRatioSteps,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(DisposeThumbSize)
                        .shadow(elevation = DisposeThumbElevation, shape = CircleShape)
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
                        .height(DisposeTrackHeight),
                ) {
                    val trackH = size.height
                    val trackW = size.width
                    val cr = CornerRadius(trackH / 2)
                    val dotRadius = DisposeTrackDotRadius.toPx()
                    val activeWidth = trackW * fraction

                    drawRoundRect(
                        color = inactiveColor,
                        size = Size(trackW, trackH),
                        cornerRadius = cr,
                    )

                    DisposeTrackDotFractions.forEach { stepFraction ->
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
private fun DisposePriceRow(
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
private fun DisposeTotalPriceRow(
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
private fun disposePercentageLabel(ratio: Float): String = when {
    ratio <= DisposeRatioMin -> stringResource(R.string.fridge_dispose_percentage_little)
    ratio <= DisposeRatioHalf -> stringResource(R.string.fridge_dispose_percentage_half)
    ratio <= DisposeRatioThreeQuarters -> stringResource(R.string.fridge_dispose_percentage_more)
    else -> stringResource(R.string.fridge_dispose_percentage_all)
}

private val disposePreviewItem = FridgeIngredientUiModel(
    id = 1L,
    name = "국내산 백오이",
    category = FoodCategory.Veg,
    quantity = 1,
    cost = 24990,
    expiryDate = LocalDate.now().plusDays(2),
)

@Preview(name = "DisposeBottomSheet - 25% (아주 조금만 폐기했어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientDisposeBottomSheetLittlePreview() {
    NeveraTheme {
        DisposeBottomSheetContent(item = disposePreviewItem, ratio = DisposeRatioMin, onRatioChange = {})
    }
}

@Preview(name = "DisposeBottomSheet - 50% (절반 정도 폐기했어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientDisposeBottomSheetHalfPreview() {
    NeveraTheme {
        DisposeBottomSheetContent(item = disposePreviewItem, ratio = DisposeRatioHalf, onRatioChange = {})
    }
}

@Preview(name = "DisposeBottomSheet - 75% (거의 다 폐기했어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientDisposeBottomSheetMorePreview() {
    NeveraTheme {
        DisposeBottomSheetContent(item = disposePreviewItem, ratio = DisposeRatioThreeQuarters, onRatioChange = {})
    }
}

@Preview(name = "DisposeBottomSheet - 100% (전부 다 폐기했어요)", showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientDisposeBottomSheetAllPreview() {
    NeveraTheme {
        DisposeBottomSheetContent(item = disposePreviewItem, ratio = DisposeRatioMax, onRatioChange = {})
    }
}
