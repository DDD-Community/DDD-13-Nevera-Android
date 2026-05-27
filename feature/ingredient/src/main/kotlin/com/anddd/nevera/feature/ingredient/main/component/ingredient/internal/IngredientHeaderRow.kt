package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 카드 헤더 행
 *
 * 원형 체크박스 + 식재료명(말줄임) + 편집 아이콘으로 구성됩니다.
 * 식재료명~편집 아이콘 영역 하단에 border 라인이 표시됩니다.
 */
@Composable
internal fun IngredientHeaderRow(
    name: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(IngredientItemCardDimension.HeaderHeight)
            .padding(horizontal = NeveraTheme.spacing.gap16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                if (isSelected) R.drawable.ic_checkbox_check_active_24
                else R.drawable.ic_checkbox_check_disabled_24
            ),
            contentDescription = stringResource(R.string.ingredient_item_checkbox_description),
            modifier = Modifier.size(NeveraTheme.iconSize.medium)
                .toggleable(
                    value = isSelected,
                    role = Role.Checkbox,
                    onValueChange = onSelectionChanged,
                ),
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
        NeveraTextField(
            value = name,
            onValueChange = {},
            modifier = Modifier.weight(1f),
            enabled = isSelected,
            useIcon = false,
            trailingIcon = {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(NeveraTheme.iconSize.xLarge),
                ) {
                    Icon(
                        painter = NeveraIcons.Edit,
                        contentDescription = stringResource(R.string.ingredient_item_edit_icon_description),
                        modifier = Modifier.size(NeveraTheme.iconSize.medium),
                        tint = NeveraTheme.colors.iconCaption,
                    )
                }
            },
            config = NeveraTextFieldConfig(
                type = NeveraTextFieldType.Underline,
                singleLine = true,
            ),
        )
    }
}

@Preview(
    name = "IngredientHeaderRow - Selected",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientHeaderRowSelectedPreview() {
    NeveraTheme {
        IngredientHeaderRow(
            name = "아침에주스 ABC 주스, 18개입 과즙 음료",
            isSelected = true,
            onSelectionChanged = {},
            onEditClick = {},
        )
    }
}

@Preview(
    name = "IngredientHeaderRow - Unselected",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientHeaderRowUnselectedPreview() {
    NeveraTheme {
        IngredientHeaderRow(
            name = "롯데 핸디카페 초콜릿 다크",
            isSelected = false,
            onSelectionChanged = {},
            onEditClick = {},
        )
    }
}
