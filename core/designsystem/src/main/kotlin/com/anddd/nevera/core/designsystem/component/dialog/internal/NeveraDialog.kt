package com.anddd.nevera.core.designsystem.component.dialog.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shadow.neveraShadow

@Composable
internal fun NeveraDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.neveraShadow(
                layers = NeveraTheme.shadow.large,
                cornerRadius = NeveraTheme.radius.xLarge,
            ),
            shape = RoundedCornerShape(size = NeveraTheme.radius.xLarge),
            color = NeveraTheme.colors.surfacePrimary,
        ) {
            Column(content = content)
        }
    }
}