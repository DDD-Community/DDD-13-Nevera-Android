package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.displayName
import com.anddd.nevera.core.ui.iconRes
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private enum class SwipeState { Settled, Revealed }
private enum class ExpiryState { Normal, Warning, DDay, Expired }

private val RevealWidth = 160.dp
private val ItemHeight = 69.dp
private val CategoryImageBorderWidth = 1.dp
private val IndicatorStrokeWidth = 2.dp
private val ExpiredBadgeOffset = 8.dp

private fun FridgeIngredientUiModel.expiryState(): ExpiryState {
    val days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)
    return when {
        days >= 8L -> ExpiryState.Normal
        days in 1..7 -> ExpiryState.Warning
        days == 0L -> ExpiryState.DDay
        else -> ExpiryState.Expired
    }
}

// D-7 → 1/8, D-6 → 2/8, … D-1 → 7/8, D-Day → 8/8
private fun sweepFraction(daysRemaining: Long): Float = (8L - daysRemaining.coerceIn(0L, 7L)) / 8f

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FridgeIngredientItem(
    item: FridgeIngredientUiModel,
    onRescueClick: () -> Unit,
    onDisposeClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val revealWidthPx = with(density) { RevealWidth.toPx() }
    val scope = rememberCoroutineScope()

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = SwipeState.Settled,
            anchors = DraggableAnchors {
                SwipeState.Settled at 0f
                SwipeState.Revealed at -revealWidthPx
            },
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = draggableState,
        positionalThreshold = { totalDistance -> totalDistance * 0.5f },
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ItemHeight),
        contentAlignment = Alignment.CenterEnd,
    ) {
        SwipeActionButtons(
            onRescueClick = {
                scope.launch { draggableState.animateTo(SwipeState.Settled) }
                onRescueClick()
            },
            onDisposeClick = {
                scope.launch { draggableState.animateTo(SwipeState.Settled) }
                onDisposeClick()
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset { IntOffset(draggableState.offset.roundToInt(), 0) }
                .anchoredDraggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    flingBehavior = flingBehavior,
                )
                .background(NeveraTheme.colors.surfacePrimary)
                .padding(
                    horizontal = NeveraTheme.spacing.padding16,
                    vertical = NeveraTheme.spacing.padding12,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12),
        ) {
            IngredientCategoryIcon(item = item)
            IngredientInfoColumn(
                modifier = Modifier.weight(1f),
                item = item,
            )
            if (item.dDayLabel.isNotEmpty()) {
                DayLabel(
                    label = item.dDayLabel,
                    isExpired = item.expiryState() == ExpiryState.Expired,
                )
            }
            Box(modifier = Modifier.fillMaxHeight()) {
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(NeveraTheme.iconSize.medium),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = NeveraTheme.colors.iconCaption,
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeActionButtons(
    onRescueClick: () -> Unit,
    onDisposeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(RevealWidth)
            .fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(NeveraTheme.colors.secondaryWeak)
                .clickable { onRescueClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.fridge_ingredient_action_rescue),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textInverse,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(NeveraTheme.colors.statusNegativeNormal)
                .clickable { onDisposeClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.fridge_ingredient_action_dispose),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textInverse,
            )
        }
    }
}

@Composable
private fun IngredientCategoryIcon(
    item: FridgeIngredientUiModel,
    modifier: Modifier = Modifier,
) {
    val expiryState = remember(item) { item.expiryState() }
    val daysRemaining = remember(item) {
        ChronoUnit.DAYS.between(LocalDate.now(), item.expiryDate)
    }
    val imageShape = RoundedCornerShape(NeveraTheme.radius.medium)
    val cornerRadiusDp: Dp = NeveraTheme.radius.medium

    val warningColor = NeveraTheme.colors.statusWarningNormal
    val negativeColor = NeveraTheme.colors.statusNegativeNormal
    val borderColor = NeveraTheme.colors.borderNormal

    Box(
        modifier = modifier.size(NeveraTheme.iconSize.xLarge),
        contentAlignment = Alignment.TopStart,
    ) {
        Image(
            painter = painterResource(item.category.iconRes()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(imageShape)
                .then(
                    if (expiryState == ExpiryState.Normal) {
                        Modifier.border(CategoryImageBorderWidth, borderColor, imageShape)
                    } else {
                        Modifier
                    }
                ),
        )

        if (expiryState != ExpiryState.Normal) {
            val arcColor = if (expiryState == ExpiryState.Expired) negativeColor else warningColor
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = IndicatorStrokeWidth.toPx()
                val cornerPx = cornerRadiusDp.toPx()
                val inset = strokePx / 2f
                val left = inset
                val top = inset
                val right = size.width - inset
                val bottom = size.height - inset

                // Path explicitly starting at 12 o'clock (top-center), going clockwise.
                // Avoids relying on addRoundRect's undefined starting point.
                val roundedRectPath = Path().apply {
                    moveTo((left + right) / 2f, top)
                    lineTo(right - cornerPx, top)
                    arcTo(Rect(right - 2 * cornerPx, top, right, top + 2 * cornerPx), -90f, 90f, false)
                    lineTo(right, bottom - cornerPx)
                    arcTo(Rect(right - 2 * cornerPx, bottom - 2 * cornerPx, right, bottom), 0f, 90f, false)
                    lineTo(left + cornerPx, bottom)
                    arcTo(Rect(left, bottom - 2 * cornerPx, left + 2 * cornerPx, bottom), 90f, 90f, false)
                    lineTo(left, top + cornerPx)
                    arcTo(Rect(left, top, left + 2 * cornerPx, top + 2 * cornerPx), 180f, 90f, false)
                    lineTo((left + right) / 2f, top)
                }

                if (expiryState == ExpiryState.DDay || expiryState == ExpiryState.Expired) {
                    drawPath(
                        path = roundedRectPath,
                        color = arcColor,
                        style = Stroke(width = strokePx),
                    )
                } else {
                    // Track: unfilled portion shown in borderNormal
                    drawPath(
                        path = roundedRectPath,
                        color = borderColor,
                        style = Stroke(width = strokePx),
                    )

                    val measure = PathMeasure()
                    measure.setPath(roundedRectPath, false)
                    val segmentLength = measure.length * sweepFraction(daysRemaining)

                    val arcPath = Path()
                    measure.getSegment(0f, segmentLength, arcPath, true)
                    drawPath(
                        path = arcPath,
                        color = arcColor,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
            }
        }

        if (expiryState == ExpiryState.Expired) {
            ExpiredBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = ExpiredBadgeOffset, y = -ExpiredBadgeOffset),
            )
        }
    }
}

@Composable
private fun ExpiredBadge(modifier: Modifier = Modifier) {
    Image(
        painter = NeveraIcons.CircleWarning,
        contentDescription = null,
        modifier = modifier.size(NeveraTheme.iconSize.small)
    )
}

@Composable
private fun IngredientInfoColumn(
    item: FridgeIngredientUiModel,
    modifier: Modifier = Modifier,
) {
    val subtitle = buildString {
        if (item.cost > 0) {
            append(stringResource(R.string.fridge_ingredient_cost_format, item.cost))
            append(" • ")
        }
        append(item.category.displayName())
        append(" • ")
        append(stringResource(R.string.fridge_ingredient_quantity_format, item.quantity))
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2),
    ) {
        Text(
            text = item.name,
            style = NeveraTheme.typography.bodyMedium,
            color = NeveraTheme.colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            style = NeveraTheme.typography.captionMedium,
            color = NeveraTheme.colors.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DayLabel(
    label: String,
    isExpired: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxHeight()) {
        Text(
            text = label,
            style = NeveraTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isExpired) NeveraTheme.colors.statusNegativeNormal
            else NeveraTheme.colors.textTertiary,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientItemPreview() {
    NeveraTheme {
        FridgeIngredientItem(
            item = FridgeIngredientUiModel(
                id = 1L,
                name = "제주 햇당근",
                category = FoodCategory.Veg,
                quantity = 1,
                cost = 6500,
                expiryDate = LocalDate.now().plusDays(28),
            ),
            onRescueClick = {},
            onDisposeClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientItemWarningPreview() {
    NeveraTheme {
        FridgeIngredientItem(
            item = FridgeIngredientUiModel(
                id = 3L,
                name = "동물복지 유정란",
                category = FoodCategory.MeatEggs,
                quantity = 4,
                cost = 8900,
                expiryDate = LocalDate.now().plusDays(4),
            ),
            onRescueClick = {},
            onDisposeClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientItemDDayPreview() {
    NeveraTheme {
        FridgeIngredientItem(
            item = FridgeIngredientUiModel(
                id = 4L,
                name = "서울우유 1L",
                category = FoodCategory.Dairy,
                quantity = 1,
                cost = 3570,
                expiryDate = LocalDate.now(),
            ),
            onRescueClick = {},
            onDisposeClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeIngredientItemExpiredPreview() {
    NeveraTheme {
        FridgeIngredientItem(
            item = FridgeIngredientUiModel(
                id = 2L,
                name = "한돈 1등급 삼겹살",
                category = FoodCategory.MeatEggs,
                quantity = 1,
                cost = 24990,
                expiryDate = LocalDate.now().minusDays(3),
            ),
            onRescueClick = {},
            onDisposeClick = {},
            onMoreClick = {},
        )
    }
}
