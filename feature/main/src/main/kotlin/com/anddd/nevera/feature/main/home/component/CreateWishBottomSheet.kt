package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraStepContentBottomSheet
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateWishBottomSheet(
    onWishCreated: (name: String, goalAmount: Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WishFormBottomSheet(
        initialName = "",
        initialAmount = "",
        confirmLabel = stringResource(R.string.home_create_wish_step2_cta),
        onWishSaved = onWishCreated,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "CreateWishBottomSheet - Step1 Normal", showBackground = true, widthDp = 360)
@Composable
private fun CreateWishBottomSheetStep1NormalPreview() {
    NeveraTheme {
        CreateWishBottomSheet(
            onWishCreated = { _, _ -> },
            onDismissRequest = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "CreateWishBottomSheet - Step1 Positive", showBackground = true, widthDp = 360)
@Composable
private fun CreateWishBottomSheetStep1PositivePreview() {
    NeveraTheme {
        var wishName by remember { mutableStateOf("에어팟 프로") }
        NeveraStepContentBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            stepIndicator = "1/2",
            title = "나만의 위시는 무엇인가요?",
            subtitle = "절약해서 이루고 싶은 걸 알려주세요",
            ctaLabel = "다음",
            ctaEnabled = true,
            onCtaClick = {},
        ) {
            NeveraTextField(
                value = wishName,
                onValueChange = { wishName = it },
                modifier = Modifier.fillMaxWidth(),
                useIcon = true,
                config = NeveraTextFieldConfig(
                    type = NeveraTextFieldType.Underline,
                    state = NeveraTextFieldState.Positive,
                    placeholder = "예: 에어팟 프로, 제주도 여행",
                    description = "1~15자로 입력해주세요",
                    singleLine = true,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "CreateWishBottomSheet - Step2 Normal", showBackground = true, widthDp = 360)
@Composable
private fun CreateWishBottomSheetStep2NormalPreview() {
    NeveraTheme {
        NeveraStepContentBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            stepIndicator = "2/2",
            title = "얼마나 모을까요?",
            subtitle = "위시 달성을 위한 목표 절약 금액을 정해요",
            backLabel = "이전",
            ctaLabel = "위시 만들기",
            ctaEnabled = false,
            onBackClick = {},
            onCtaClick = {},
        ) {
            NeveraTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                useIcon = true,
                suffix = { NeveraTextFieldSuffix("원") },
                config = NeveraTextFieldConfig(
                    type = NeveraTextFieldType.Underline,
                    state = NeveraTextFieldState.Normal,
                    placeholder = "0",
                    description = "최대 10자리 금액까지 입력해주세요",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ),
            )
        }
    }
}
