package com.anddd.nevera.core.ui.component.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraActionBottomSheet
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R
import com.anddd.nevera.core.ui.displayName
import com.anddd.nevera.core.ui.iconRes
import com.anddd.nevera.domain.model.ingredient.FoodCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    selectedCategory: FoodCategory?,
    onCategorySelected: (FoodCategory) -> Unit,
    onDismiss: () -> Unit,
) {
    var tempSelected by remember(selectedCategory) { mutableStateOf(selectedCategory) }
    val sheetState = rememberModalBottomSheetState()

    NeveraActionBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.ingredient_category_title),
        confirmLabel = stringResource(R.string.ingredient_category_confirm),
        onConfirm = {
            tempSelected?.let(onCategorySelected)
            onDismiss()
        },
        onDismissRequest = onDismiss,
    ) {
        CategoryList(
            selectedCategory = tempSelected,
            onCategoryClick = { tempSelected = it },
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

@Composable
private fun CategoryList(
    selectedCategory: FoodCategory?,
    onCategoryClick: (FoodCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(FoodCategory.entries) { category ->
            CategoryItem(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategoryClick(category) },
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: FoodCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = NeveraTheme.spacing.padding20)
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.background(NeveraTheme.colors.surfaceSecondary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(category.iconRes()),
                contentDescription = null,
                modifier = Modifier.size(NeveraTheme.iconSize.large),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))

        Text(
            text = category.displayName(),
            style = NeveraTheme.typography.bodyLarge,
            color = NeveraTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = NeveraIcons.Check,
            contentDescription = null,
            tint = if (isSelected) NeveraTheme.colors.primaryNormal
            else NeveraTheme.colors.iconDisabled,
            modifier = Modifier.size(NeveraTheme.iconSize.medium),
        )
    }
}
