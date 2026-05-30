package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraContentBottomSheet
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.feature.main.R

private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9]{2,6}$")

private fun nicknameFieldState(nickname: String): NeveraTextFieldState = when {
    nickname.isEmpty() -> NeveraTextFieldState.Normal
    NICKNAME_REGEX.matches(nickname) -> NeveraTextFieldState.Positive
    else -> NeveraTextFieldState.Negative
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SetNicknameBottomSheet(
    onNicknameConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nickname by remember { mutableStateOf("") }
    val fieldState = nicknameFieldState(nickname)
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.Hidden },
    )

    NeveraContentBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        title = stringResource(R.string.home_set_nickname_title),
        subtitle = stringResource(R.string.home_set_nickname_subtitle),
        cta = stringResource(R.string.home_set_nickname_cta),
        ctaEnabled = fieldState == NeveraTextFieldState.Positive,
        onCtaClick = { onNicknameConfirmed(nickname) },
        onDismissRequest = { },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
    ) {
        NeveraTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.fillMaxWidth(),
            config = NeveraTextFieldConfig(
                type = NeveraTextFieldType.Underline,
                state = fieldState,
                placeholder = stringResource(R.string.home_set_nickname_placeholder),
                description = stringResource(R.string.home_set_nickname_description),
                singleLine = true,
            ),
        )
    }
}
