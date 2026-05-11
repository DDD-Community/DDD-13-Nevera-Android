package com.anddd.nevera.core.designsystem.component.textfield.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun NeveraBaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useIcon: Boolean = true,
    isPassword: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isActive = value.isNotEmpty()
    var eyeVisible by rememberSaveable { mutableStateOf(false) }

    val visualTransformation = when {
        isPassword && !eyeVisible -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }

    val borderColor = NeveraTextFieldColors.borderColor(
        type = config.type,
        state = config.state,
        isFocused = isFocused,
        isActive = isActive,
        enabled = enabled,
        negativeColor = config.negativeColor,
    )
    val containerColor = NeveraTextFieldColors.containerColor(config.type)
    val headingColor = NeveraTextFieldColors.headingColor()
    val inputTextColor = NeveraTextFieldColors.inputTextColor(enabled)
    val placeholderColor = NeveraTextFieldColors.placeholderColor(config.type, enabled)
    val descriptionColor = NeveraTextFieldColors.descriptionColor(config.state, enabled, config.negativeColor)

    val contentPadding = when (config.type) {
        NeveraTextFieldType.Box -> NeveraTextFieldDefaults.BoxContentPadding
        NeveraTextFieldType.Underline -> NeveraTextFieldDefaults.UnderlineContentPadding
    }

    val textStyle = when (config.type) {
        NeveraTextFieldType.Box -> NeveraTheme.typography.bodyLarge
        NeveraTextFieldType.Underline -> NeveraTheme.typography.titleLarge
    }

    val containerModifier = when (config.type) {
        NeveraTextFieldType.Box -> Modifier
            .background(containerColor, NeveraTextFieldDefaults.BoxShape)
            .border(NeveraTextFieldDefaults.BorderWidth, borderColor, NeveraTextFieldDefaults.BoxShape)

        // border()는 사방 테두리를 그리므로 drawBehind로 하단선만 직접 그린다.
        NeveraTextFieldType.Underline -> Modifier
            .background(containerColor)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = NeveraTextFieldDefaults.BorderWidth.toPx(),
                )
            }
    }

    Column(modifier = modifier) {
        config.heading?.let { heading ->
            Text(
                text = heading,
                modifier = Modifier.padding(start = NeveraTextFieldDefaults.LabelStartPadding),
                style = NeveraTheme.typography.titleXSmall,
                color = headingColor,
            )
            Spacer(modifier = Modifier.height(NeveraTextFieldDefaults.HeadingBottomGap))
        }

        // Material3 TextField/OutlinedTextField는 내부 padding·color·shape이 고정되어 Underline 타입과 커스텀 배경 적용이 불가능하다.
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            enabled = enabled,
            textStyle = textStyle.copy(color = inputTextColor),
            keyboardOptions = config.keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = config.singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = containerModifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && config.placeholder != null) {
                            Text(
                                text = config.placeholder,
                                style = textStyle,
                                color = placeholderColor,
                            )
                        }
                        // innerTextField()를 호출하지 않으면 커서가 렌더링되지 않는다.
                        innerTextField()
                    }
                    TrailingIcons(
                        state = config.state,
                        isActive = isActive,
                        useIcon = useIcon,
                        isPassword = isPassword,
                        eyeVisible = eyeVisible,
                        enabled = enabled,
                        negativeColor = config.negativeColor,
                        onEyeIconClick = { eyeVisible = !eyeVisible },
                    )
                }
            },
        )

        config.description?.let { description ->
            Spacer(modifier = Modifier.height(NeveraTextFieldDefaults.DescriptionTopGap))
            Text(
                text = description,
                modifier = Modifier.padding(start = NeveraTextFieldDefaults.LabelStartPadding),
                style = NeveraTheme.typography.captionLarge,
                color = descriptionColor,
            )
        }
    }
}

@Composable
private fun TrailingIcons(
    state: NeveraTextFieldState,
    isActive: Boolean,
    useIcon: Boolean,
    isPassword: Boolean,
    eyeVisible: Boolean,
    enabled: Boolean,
    negativeColor: Color,
    onEyeIconClick: () -> Unit,
) {
    val showCheckIcon = useIcon && state == NeveraTextFieldState.Positive && isActive
    val showWarningIcon = useIcon && state == NeveraTextFieldState.Negative
    val showEyeIcon = useIcon && isPassword
    if (!showCheckIcon && !showWarningIcon && !showEyeIcon) return

    val stateIconColor = NeveraTextFieldColors.stateIconColor(state, negativeColor)
    val eyeIconColor = NeveraTextFieldColors.eyeIconColor(enabled)

    Row(
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showCheckIcon) {
            Icon(
                painter = NeveraIcons.Check,
                contentDescription = "입력이 올바릅니다",
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
                tint = stateIconColor,
            )
        }
        if (showWarningIcon) {
            Icon(
                painter = NeveraIcons.Warning,
                contentDescription = "입력을 확인하세요",
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
                tint = stateIconColor,
            )
        }
        if (showEyeIcon) {
            // IconButton은 Material3 최소 터치 타겟(48dp)을 강제해 필드 높이를 팽창시키므로 Box+clickable로 대체.
            Box(
                modifier = Modifier
                    .size(NeveraTheme.iconSize.medium)
                    .clip(CircleShape)
                    .clickable(
                        enabled = enabled,
                        onClick = onEyeIconClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = if (eyeVisible) NeveraIcons.EyeOff else NeveraIcons.Eye,
                    contentDescription = if (eyeVisible) "비밀번호 숨기기" else "비밀번호 표시",
                    modifier = Modifier.size(NeveraTheme.iconSize.medium),
                    tint = eyeIconColor,
                )
            }
        }
    }
}
