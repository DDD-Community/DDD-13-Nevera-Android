package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 식재료 이름 편집 다이얼로그
 *
 * TODO: 디자이너와 추가 논의 후 인라인 편집 방식으로 개선 예정
 */
@Composable
internal fun IngredientNameEditDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var editingName by remember(currentName) { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NeveraTheme.colors.surfacePrimary,
        shape = RoundedCornerShape(NeveraTheme.radius.medium),
        title = {
            Text(
                text = stringResource(R.string.ingredient_item_edit_name),
                style = NeveraTheme.typography.titleMedium,
                color = NeveraTheme.colors.textPrimary,
            )
        },
        text = {
            NeveraTextField(
                value = editingName,
                onValueChange = { editingName = it },
                useIcon = false,
                config = NeveraTextFieldConfig(
                    placeholder = stringResource(R.string.ingredient_item_edit_name_placeholder),
                    singleLine = true,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(editingName) }) {
                Text(
                    text = stringResource(R.string.ingredient_item_edit_confirm),
                    color = NeveraTheme.colors.primaryNormal,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.ingredient_item_edit_dismiss),
                    color = NeveraTheme.colors.textSecondary,
                )
            }
        },
    )
}

@Preview(
    name = "IngredientNameEditDialog",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientNameEditDialogPreview() {
    NeveraTheme {
        IngredientNameEditDialog(
            currentName = "아침에주스 ABC 주스",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
