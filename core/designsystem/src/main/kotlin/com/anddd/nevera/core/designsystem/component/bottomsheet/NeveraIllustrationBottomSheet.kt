package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraGhostButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

enum class NeveraIllustrationActionLayout { Column, Row }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraIllustrationBottomSheet(
    sheetState: SheetState,
    illustration: @Composable () -> Unit,
    title: String,
    subtitle: String,
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    ghostLabel: String? = null,
    onGhostClick: (() -> Unit)? = null,
    actionLayout: NeveraIllustrationActionLayout = NeveraIllustrationActionLayout.Column,
) {
    NeveraBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        NeveraIllustrationBottomSheetContent(
            illustration = illustration,
            title = title,
            subtitle = subtitle,
            primaryLabel = primaryLabel,
            onPrimaryClick = onPrimaryClick,
            ghostLabel = ghostLabel,
            onGhostClick = onGhostClick,
            actionLayout = actionLayout,
        )
    }
}

@Composable
private fun NeveraIllustrationBottomSheetContent(
    illustration: @Composable () -> Unit,
    title: String,
    subtitle: String,
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    ghostLabel: String? = null,
    onGhostClick: (() -> Unit)? = null,
    actionLayout: NeveraIllustrationActionLayout = NeveraIllustrationActionLayout.Column,
) {
    val hasGhost = ghostLabel != null && onGhostClick != null
    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            illustration()
        }
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = NeveraTheme.colors.textSecondary,
            style = NeveraTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap4))
        Text(
            text = subtitle,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = NeveraTheme.colors.textQuaternary,
            style = NeveraTheme.typography.bodySmall,
        )
        if (actionLayout == NeveraIllustrationActionLayout.Row && hasGhost) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = NeveraTheme.spacing.padding20,
                        end = NeveraTheme.spacing.padding20,
                        top = NeveraTheme.spacing.gap20,
                        bottom = NeveraTheme.spacing.padding16,
                    ),
                horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12),
            ) {
                NeveraGhostButton(
                    label = ghostLabel!!,
                    onClick = onGhostClick!!,
                    modifier = Modifier.weight(1f),
                    color = NeveraButtonColor.Secondary,
                )
                NeveraFilledButton(
                    label = primaryLabel,
                    onClick = onPrimaryClick,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            NeveraFilledButton(
                label = primaryLabel,
                onClick = onPrimaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = NeveraTheme.spacing.padding20,
                        end = NeveraTheme.spacing.padding20,
                        top = NeveraTheme.spacing.gap20,
                        bottom = if (hasGhost) NeveraTheme.spacing.gap8 else NeveraTheme.spacing.padding16,
                    ),
            )
            if (hasGhost) {
                NeveraGhostButton(
                    label = ghostLabel!!,
                    onClick = onGhostClick!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = NeveraTheme.spacing.padding20,
                            end = NeveraTheme.spacing.padding20,
                            bottom = NeveraTheme.spacing.padding16,
                        ),
                    color = NeveraButtonColor.Secondary,
                )
            }
        }
    }
}

@Preview(name = "Column - ghost 있음", showBackground = true, widthDp = 360)
@Composable
private fun IllustrationBottomSheetColumnWithGhostPreview() {
    NeveraTheme {
        NeveraIllustrationBottomSheetContent(
            illustration = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NeveraTheme.spacing.padding20 * 10),
                )
            },
            title = "식구에 오신 걸 환영해요!",
            subtitle = "식재료를 구조하고 절약한 금액으로\n원하는 것을 이뤄보세요",
            primaryLabel = "위시 만들기",
            onPrimaryClick = {},
            ghostLabel = "건너뛰기",
            onGhostClick = {},
        )
    }
}

@Preview(name = "Column - ghost 없음", showBackground = true, widthDp = 360)
@Composable
private fun IllustrationBottomSheetColumnNoGhostPreview() {
    NeveraTheme {
        NeveraIllustrationBottomSheetContent(
            illustration = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NeveraTheme.spacing.padding20 * 10)
                )
            },
            title = "식구에 오신 걸 환영해요!",
            subtitle = "식재료를 구조하고 절약한 금액으로\n원하는 것을 이뤄보세요",
            primaryLabel = "위시 만들기",
            onPrimaryClick = {},
        )
    }
}

@Preview(name = "Row - ghost 있음", showBackground = true, widthDp = 360)
@Composable
private fun IllustrationBottomSheetRowWithGhostPreview() {
    NeveraTheme {
        NeveraIllustrationBottomSheetContent(
            illustration = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NeveraTheme.spacing.padding20 * 10)
                )
            },
            title = "식구에 오신 걸 환영해요!",
            subtitle = "식재료를 구조하고 절약한 금액으로\n원하는 것을 이뤄보세요",
            primaryLabel = "위시 만들기",
            onPrimaryClick = {},
            ghostLabel = "건너뛰기",
            onGhostClick = {},
            actionLayout = NeveraIllustrationActionLayout.Row,
        )
    }
}

@Preview(name = "Row - ghost 없음 (Column으로 폴백)", showBackground = true, widthDp = 360)
@Composable
private fun IllustrationBottomSheetRowNoGhostPreview() {
    NeveraTheme {
        NeveraIllustrationBottomSheetContent(
            illustration = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NeveraTheme.spacing.padding20 * 10)
                )
            },
            title = "식구에 오신 걸 환영해요!",
            subtitle = "식재료를 구조하고 절약한 금액으로\n원하는 것을 이뤄보세요",
            primaryLabel = "위시 만들기",
            onPrimaryClick = {},
            actionLayout = NeveraIllustrationActionLayout.Row,
        )
    }
}
