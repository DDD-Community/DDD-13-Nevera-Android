package com.anddd.nevera.core.designsystem.component.timepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraWeakButton
import com.anddd.nevera.core.designsystem.component.dialog.internal.NeveraDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

private val PICKER_ITEM_HEIGHT = 48.dp
private val PICKER_VISIBLE_ITEMS = 5
private val PICKER_HEIGHT = PICKER_ITEM_HEIGHT * PICKER_VISIBLE_ITEMS

/**
 * Nevera 디자인 시스템 시간 선택 다이얼로그
 *
 * 드럼롤 스크롤 방식으로 오전/오후, 시(1-12), 분(00/30)을 선택합니다.
 *
 * @param initialHour   초기 시간 (0-23, 24시간 기준)
 * @param initialMinute 초기 분 (0 or 30)
 * @param onTimeSelected 완료 클릭 시 선택된 시간 전달 (hour: 0-23, minute: 0 or 30)
 * @param onDismiss     취소 또는 바깥 클릭 시 호출
 */
@Composable
fun NeveraTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val meridiemOptions = listOf(stringResource(R.string.nevera_time_picker_am), stringResource(R.string.nevera_time_picker_pm))
    val hourOptions = (1..12).map { it.toString() }
    val minuteOptions = listOf("00", "30")

    val initialIsPm = initialHour >= 12
    val initialHour12 = when {
        initialHour == 0 -> 12
        initialHour > 12 -> initialHour - 12
        else -> initialHour
    }
    val initialMinuteIndex = if (initialMinute >= 30) 1 else 0

    val meridiemState = rememberLazyListState(initialFirstVisibleItemIndex = if (initialIsPm) 1 else 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = initialHour12 - 1)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)

    val selectedMeridiemIndex by remember { derivedStateOf { meridiemState.firstVisibleItemIndex } }
    val selectedHourIndex by remember { derivedStateOf { hourState.firstVisibleItemIndex } }
    val selectedMinuteIndex by remember { derivedStateOf { minuteState.firstVisibleItemIndex } }

    NeveraDialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(top = NeveraTheme.spacing.padding20)) {
            Text(
                text = stringResource(R.string.nevera_time_picker_title),
                style = NeveraTheme.typography.bodySmall,
                color = NeveraTheme.colors.textQuaternary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NeveraTheme.spacing.padding20),
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PICKER_HEIGHT),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PICKER_ITEM_HEIGHT)
                        .align(Alignment.Center)
                        .background(NeveraTheme.colors.surfaceSecondary),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PICKER_HEIGHT)
                        .padding(horizontal = NeveraTheme.spacing.padding20),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PickerColumn(
                        items = meridiemOptions,
                        listState = meridiemState,
                        selectedIndex = selectedMeridiemIndex,
                        modifier = Modifier.weight(1f),
                    )
                    PickerColumn(
                        items = hourOptions,
                        listState = hourState,
                        selectedIndex = selectedHourIndex,
                        modifier = Modifier.weight(1f),
                    )
                    PickerColumn(
                        items = minuteOptions,
                        listState = minuteState,
                        selectedIndex = selectedMinuteIndex,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NeveraTheme.spacing.padding16),
            ) {
                NeveraWeakButton(
                    label = stringResource(R.string.nevera_time_picker_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    color = NeveraButtonColor.Primary,
                    size = NeveraButtonSize.Large,
                )
                Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
                NeveraFilledButton(
                    label = stringResource(R.string.nevera_time_picker_confirm),
                    onClick = {
                        val isPm = selectedMeridiemIndex == 1
                        val hour12 = selectedHourIndex + 1
                        val hour24 = when {
                            !isPm && hour12 == 12 -> 0
                            isPm && hour12 != 12 -> hour12 + 12
                            else -> hour12
                        }
                        val minute = if (selectedMinuteIndex == 0) 0 else 30
                        onTimeSelected(hour24, minute)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    color = NeveraButtonColor.Primary,
                    size = NeveraButtonSize.Large,
                )
            }
        }
    }
}

@Composable
private fun PickerColumn(
    items: List<String>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
) {
    val paddingItems = 2

    LazyColumn(
        state = listState,
        modifier = modifier.height(PICKER_HEIGHT),
        userScrollEnabled = true,
    ) {
        items(paddingItems) {
            Box(modifier = Modifier.height(PICKER_ITEM_HEIGHT))
        }
        items(items.size) { index ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PICKER_ITEM_HEIGHT),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = items[index],
                    style = if (isSelected) NeveraTheme.typography.headlineSmall else NeveraTheme.typography.titleLarge,
                    color = if (isSelected) NeveraTheme.colors.textSecondary else NeveraTheme.colors.textDisabled,
                    textAlign = TextAlign.Center,
                )
            }
        }
        items(paddingItems) {
            Box(modifier = Modifier.height(PICKER_ITEM_HEIGHT))
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisible = listState.firstVisibleItemScrollOffset
            val itemHeight = PICKER_ITEM_HEIGHT.value.toInt()
            val targetIndex = listState.firstVisibleItemIndex + if (firstVisible > itemHeight / 2) 1 else 0
            listState.animateScrollToItem(targetIndex)
        }
    }
}

@Preview(name = "NeveraTimePickerDialog - 오후 06:00", showBackground = true)
@Composable
private fun NeveraTimePickerDialogPm6Preview() {
    NeveraTheme {
        NeveraTimePickerDialog(
            initialHour = 18,
            initialMinute = 0,
            onTimeSelected = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(name = "NeveraTimePickerDialog - 오전 11:00", showBackground = true)
@Composable
private fun NeveraTimePickerDialogAm11Preview() {
    NeveraTheme {
        NeveraTimePickerDialog(
            initialHour = 11,
            initialMinute = 0,
            onTimeSelected = { _, _ -> },
            onDismiss = {},
        )
    }
}
