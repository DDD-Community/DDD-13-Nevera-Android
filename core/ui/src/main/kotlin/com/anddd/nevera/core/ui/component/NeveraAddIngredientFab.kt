package com.anddd.nevera.core.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledLeadingIconButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraAddIngredientFab(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NeveraFilledLeadingIconButton(
        painter = NeveraIcons.Plus,
        contentDescription = "",
        label = label,
        onClick = onClick,
        modifier = modifier,
        size = NeveraButtonSize.Medium,
        shape = RoundedCornerShape(NeveraTheme.radius.max),
    )
}

@Preview(showBackground = true)
@Composable
private fun NeveraAddIngredientFabPreview() {
    NeveraTheme {
        NeveraAddIngredientFab(
            label = "식재료",
            onClick = {},
        )
    }
}
