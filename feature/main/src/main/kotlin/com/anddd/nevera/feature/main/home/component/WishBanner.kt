package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R

private val RescuerSize = 130.dp
private val ProgressBarHeight = 8.dp

@Composable
fun WishBanner(
    nickname: String,
    wish: String,
    savedMoney: Int,
    goalMoney: Int,
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
                text = "${nickname}님!\n함께 식재료를 구조해요",
                style = NeveraTheme.typography.titleLarge,
                color = NeveraTheme.colors.textPrimary,
                modifier = Modifier.padding(NeveraTheme.spacing.padding8)
            )
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.padding8))
            WishCard(wish = wish, savedMoney = savedMoney, goalMoney = goalMoney)
            // TODO EmptyWishCard Impl
        }
    }
}

@Composable
private fun WishCard(
    wish: String,
    savedMoney: Int,
    goalMoney: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (goalMoney > 0) savedMoney.toFloat() / goalMoney else 0f
    val remaining = goalMoney - savedMoney
    val cardShape = RoundedCornerShape(NeveraTheme.radius.medium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NeveraTheme.colors.surfacePrimary)
            .border(1.dp, NeveraTheme.colors.borderNormal, cardShape)
            .padding(NeveraTheme.spacing.padding16),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "%,d원".format(savedMoney),
                style = NeveraTheme.typography.headlineSmall,
                color = NeveraTheme.colors.textSecondary,
            )
            Text(
                text = "/ %,d원".format(goalMoney),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textCaption,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Spacer(Modifier.height(NeveraTheme.spacing.gap12))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(ProgressBarHeight)
                .clip(RoundedCornerShape(NeveraTheme.radius.max)),
            color = NeveraTheme.colors.primaryNormal,
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
                contentDescription = "wish flag",
                modifier = Modifier.size(NeveraTheme.iconSize.small)
            )
            Spacer(Modifier.width(NeveraTheme.spacing.gap8))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = NeveraTheme.colors.primaryNormal)) {
                        append(wish)
                    }
                    append("까지 %,d원 남았어요".format(remaining))
                },
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textTertiary,
            )
        }
    }
}

@Preview(
    name = "WishBanner",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun WishBannerPreview() {
    NeveraTheme {
        WishBanner(
            nickname = "김푸드",
            wish = "제주도 여행",
            savedMoney = 0,
            goalMoney = 480000,
        )
    }
}
