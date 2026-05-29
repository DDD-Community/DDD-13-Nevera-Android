package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.model.HomeWishUiModel

private val RescuerSize = 130.dp
private val ProgressBarHeight = 8.dp
private val BorderWidth = 1.dp
private val BorderDashPathSize = 6.dp
private val BorderDashPathEmptySize = 6.dp
private val AddWishButtonHeight = 34.dp
private val WishBottomPadding = 2.dp

@Composable
internal fun WishBanner(
    nickname: String,
    wish: HomeWishUiModel?,
    onCreateWish: () -> Unit,
    onEditWish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.ill_rescuer),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(RescuerSize)
                .padding(end = NeveraTheme.spacing.padding8),
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))
            Text(
                text = stringResource(R.string.home_wish_banner_greeting, nickname),
                style = NeveraTheme.typography.titleLarge,
                color = NeveraTheme.colors.textPrimary,
                modifier = Modifier.padding(NeveraTheme.spacing.padding8)
            )
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.padding8))
            wish?.let {
                WishCard(
                    wish = it.name,
                    savedMoney = it.accumulatedAmount,
                    goalMoney = it.goalAmount,
                    remainingMoney = it.remainingAmount,
                    onClick = onEditWish,
                )
            } ?: run {
                EmptyWishCard(onCreateWish = onCreateWish)
            }
        }
    }
}

@Composable
private fun WishCard(
    wish: String,
    savedMoney: Int,
    goalMoney: Int,
    remainingMoney: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (goalMoney > 0) (savedMoney.toFloat() / goalMoney).coerceIn(0f, 1f) else 0f
    val cardShape = RoundedCornerShape(NeveraTheme.radius.medium)
    val remainingText = stringResource(R.string.home_wish_remaining_amount, remainingMoney)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NeveraTheme.colors.surfacePrimary)
            .border(BorderWidth, NeveraTheme.colors.borderNormal, cardShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(NeveraTheme.spacing.padding16),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2)
        ) {
            Text(
                text = stringResource(R.string.home_wish_amount_format, savedMoney),
                style = NeveraTheme.typography.headlineSmall,
                color = NeveraTheme.colors.textSecondary,
            )
            Text(
                text = stringResource(R.string.home_wish_amount_separator),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textCaption,
                modifier = Modifier.padding(bottom = WishBottomPadding),
            )
            Text(
                text = stringResource(R.string.home_wish_amount_format, goalMoney),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textCaption,
                modifier = Modifier.padding(bottom = WishBottomPadding),
            )
        }
        Spacer(Modifier.height(NeveraTheme.spacing.gap12))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(ProgressBarHeight)
                .clip(RoundedCornerShape(NeveraTheme.radius.max)),
            color = NeveraTheme.colors.primaryWeak,
            trackColor = NeveraTheme.colors.surfaceSecondary,
            drawStopIndicator = {},
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        HorizontalDivider(
            color = NeveraTheme.colors.dividerNormal,
            thickness = 1.dp,
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_flag),
                contentDescription = stringResource(R.string.home_wish_flag_icon_description),
                modifier = Modifier.size(NeveraTheme.iconSize.small)
            )
            Spacer(Modifier.width(NeveraTheme.spacing.gap8))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = NeveraTheme.colors.primaryNormal)) {
                        append(wish)
                    }
                    append(remainingText)
                },
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textTertiary,
            )
        }
    }
}

@Composable
private fun EmptyWishCard(
    onCreateWish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = NeveraTheme.colors.borderStrong

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NeveraTheme.colors.surfacePrimary)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(
                        width = BorderWidth.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(BorderDashPathSize.toPx(), BorderDashPathEmptySize.toPx()),
                            phase = 0f
                        ),
                    ),
                    cornerRadius = CornerRadius(NeveraTheme.radius.large.toPx()),
                )
            }
            .padding(vertical = NeveraTheme.spacing.padding32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
    ) {
        Text(
            text = stringResource(R.string.home_wish_empty_description),
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textCaption,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(NeveraTheme.radius.max))
                .background(NeveraTheme.colors.textPrimary)
                .clickable(onClick = onCreateWish)
                .padding(horizontal = NeveraTheme.spacing.padding8)
                .height(AddWishButtonHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = NeveraIcons.CirclePlus,
                contentDescription = stringResource(R.string.home_wish_add_icon_description),
                modifier = Modifier.size(NeveraTheme.iconSize.xSmall),
                colorFilter = ColorFilter.tint(color = NeveraTheme.colors.textInverse)
            )
            Text(
                text = stringResource(R.string.home_wish_create_button),
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textInverse,
                modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding6)
            )
        }
    }
}

@Preview(
    name = "WishBanner - 위시 있음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun WishBannerWithWishPreview() {
    NeveraTheme {
        WishBanner(
            nickname = "닉네임",
            wish = HomeWishUiModel(
                id = 1L,
                name = "새 맥북",
                goalAmount = 2000000,
                accumulatedAmount = 1200000,
                remainingAmount = 800000,
                isAchieved = false,
            ),
            onCreateWish = {},
            onEditWish = {},
        )
    }
}

@Preview(
    name = "WishBanner - 위시 달성",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun WishBannerAchievedPreview() {
    NeveraTheme {
        WishBanner(
            nickname = "닉네임",
            wish = HomeWishUiModel(
                id = 1L,
                name = "새 맥북",
                goalAmount = 2000000,
                accumulatedAmount = 2000000,
                remainingAmount = 0,
                isAchieved = true,
            ),
            onCreateWish = {},
            onEditWish = {},
        )
    }
}

@Preview(
    name = "WishBanner - 위시 없음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun WishBannerEmptyPreview() {
    NeveraTheme {
        WishBanner(
            nickname = "닉네임",
            wish = null,
            onCreateWish = {},
            onEditWish = {},
        )
    }
}
