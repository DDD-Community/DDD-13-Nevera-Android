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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
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
            contentDescription = null,
            modifier = Modifier.size(NeveraTheme.iconSize.medium)
                .toggleable(
                    value = isSelected,
                    role = Role.Checkbox,
                    onValueChange = onSelectionChanged,
                ),
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
        val borderColor = NeveraTheme.colors.borderNormal
        Row(
            modifier = Modifier.weight(1f)
                .heightIn(IngredientItemCardDimension.NameRowHeight)
                .drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = IngredientItemCardDimension.BorderStrokeWidth.toPx(),
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                color = NeveraTheme.colors.textSecondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = NeveraTheme.typography.titleLarge,
            )
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
        }
    }
}
