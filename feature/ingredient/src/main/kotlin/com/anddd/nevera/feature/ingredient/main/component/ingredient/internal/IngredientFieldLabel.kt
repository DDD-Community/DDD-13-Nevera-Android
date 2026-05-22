package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun IngredientFieldLabel(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        style = NeveraTheme.typography.titleXSmall,
        color = NeveraTheme.colors.textCaption,
        modifier = Modifier.width(IngredientItemCardDimension.FieldLabelWidth),
    )
}
