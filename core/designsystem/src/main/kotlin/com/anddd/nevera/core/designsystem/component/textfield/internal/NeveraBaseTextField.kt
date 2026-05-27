package com.anddd.nevera.core.designsystem.component.textfield.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * NeveraTextField 계열의 공통 렌더링 구현체. 공개 API에서 직접 사용하지 않는다.
 *
 * ## 텍스트 말줄임 전략
 * `BasicTextField(singleLine=true)`는 `TextOverflow.Ellipsis`를 무시한다. (Compose 제약)
 * unfocused/disabled → [StaticTextContent](Text + 말줄임), focused → [ActiveTextContent](innerTextField)
 *
 * ## 커서 정책
 * - [autoMoveCursor] = true (기본): 포커스 획득 시 커서를 텍스트 맨 뒤로, 해제 시 맨 앞으로 이동.
 *   String 기반 공개 오버로드에서 사용하는 기본 정책이다.
 * - [autoMoveCursor] = false: 커서 위치를 [onTextFieldValueChange] 콜백으로 받은 [TextFieldValue] 그대로 유지.
 *   [TextFieldValue] 기반 공개 오버로드에서 호출자가 커서를 직접 제어할 때 사용한다.
 *
 * ## 오버로드 구조 (공개 API → 이 함수)
 * ```
 * NeveraTextField(String)         → NeveraTextField(TextFieldValue, autoMoveCursor=true) → NeveraBaseTextField
 * NeveraTextField(TextFieldValue) → NeveraBaseTextField(autoMoveCursor=false, 기본값)
 * ```
 *
 * @param textFieldValue 커서·선택 정보를 포함한 현재 입력 상태. String 오버로드에서는 내부 관리됨.
 * @param onTextFieldValueChange 입력 상태(텍스트·커서·선택) 변경 콜백
 * @param useIcon true이면 state에 따라 check/warning 아이콘을 trailing에 표시
 * @param visualTransformation 텍스트 시각 변환. 비밀번호 필드는 [PasswordVisualTransformation]을 전달한다.
 * @param trailingIcon trailing 영역에 삽입할 슬롯 람다. check/warning 아이콘 우측에 렌더링된다. null이면 미표시.
 * @param autoMoveCursor 포커스 기반 자동 커서 이동 정책 활성화 여부
 * @param suffix trailing 영역 우측에 렌더링할 추가 컨텐츠. NeveraTextFieldSuffix 활용 권장.
 */
@Composable
internal fun NeveraBaseTextField(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useIcon: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    autoMoveCursor: Boolean = true,
    suffix: (@Composable () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isActive = textFieldValue.text.isNotEmpty()

    AutoMoveCursorEffect(
        enabled = autoMoveCursor,
        isFocused = isFocused,
        textFieldValue = textFieldValue,
        onTextFieldValueChange = onTextFieldValueChange,
    )

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
        NeveraTextFieldType.Box -> Modifier.background(
            containerColor,
            NeveraTextFieldDefaults.BoxShape
        ).border(
            width = NeveraTextFieldDefaults.BorderWidth,
            color = borderColor,
            shape = NeveraTextFieldDefaults.BoxShape
        )

        // border()는 사방 테두리를 그리므로 drawBehind로 하단선만 직접 그린다.
        NeveraTextFieldType.Underline -> Modifier.background(containerColor)
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
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            modifier = Modifier.fillMaxWidth()
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
                    modifier = containerModifier.fillMaxWidth()
                        .padding(contentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextInputContent(
                        innerTextField = innerTextField,
                        enabled = enabled,
                        isFocused = isFocused,
                        text = textFieldValue.text,
                        placeholder = config.placeholder,
                        singleLine = config.singleLine,
                        textStyle = textStyle,
                        inputTextColor = inputTextColor,
                        placeholderColor = placeholderColor,
                        visualTransformation = visualTransformation,
                        modifier = Modifier.weight(1f),
                    )
                    TrailingIcons(
                        state = config.state,
                        isActive = isActive,
                        useIcon = useIcon,
                        negativeColor = config.negativeColor,
                        trailingIcon = trailingIcon,
                    )
                    suffix?.invoke()
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

/**
 * 포커스 상태에 따라 [StaticTextContent] ↔ [ActiveTextContent]를 전환하는 컨테이너.
 * unfocused/disabled → Text로 말줄임 표시, focused → innerTextField로 커서·편집 활성화.
 *
 * `decorationBox` 계약상 innerTextField()는 반드시 1회 호출해야 하므로,
 * unfocused 분기에서 0dp Box로 숨겨서 호출한다. (탭·포커스 동작에 영향 없음)
 */
@Composable
private fun TextInputContent(
    innerTextField: @Composable () -> Unit,
    enabled: Boolean,
    isFocused: Boolean,
    text: String,
    placeholder: String?,
    singleLine: Boolean,
    textStyle: TextStyle,
    inputTextColor: Color,
    placeholderColor: Color,
    visualTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (!enabled || !isFocused) {
            // 정적 표시: placeholder 또는 말줄임 텍스트
            StaticTextContent(
                text = text,
                placeholder = placeholder,
                singleLine = singleLine,
                textStyle = textStyle,
                inputTextColor = inputTextColor,
                placeholderColor = placeholderColor,
                visualTransformation = visualTransformation,
            )
            // BasicTextField 계약: innerTextField()는 반드시 1회 호출 → 0dp Box로 숨김
            Box(Modifier.size(0.dp)) { innerTextField() }
        } else {
            // 활성 입력: 커서·편집·스크롤 활성화
            ActiveTextContent(
                innerTextField = innerTextField,
                text = text,
                placeholder = placeholder,
                textStyle = textStyle,
                placeholderColor = placeholderColor,
            )
        }
    }
}

/**
 * unfocused/disabled 상태의 정적 텍스트 표시.
 * 빈 텍스트 → placeholder, 입력 있음 → visualTransformation 적용 후 말줄임.
 * visualTransformation 미적용 시 비밀번호 평문 노출에 주의.
 */
@Composable
private fun StaticTextContent(
    text: String,
    placeholder: String?,
    singleLine: Boolean,
    textStyle: TextStyle,
    inputTextColor: Color,
    placeholderColor: Color,
    visualTransformation: VisualTransformation,
) {
    if (text.isEmpty() && placeholder != null) {
        Text(text = placeholder, style = textStyle, color = placeholderColor)
    } else {
        val displayText = remember(text, visualTransformation) {
            visualTransformation.filter(AnnotatedString(text)).text.text
        }
        Text(
            text = displayText,
            style = textStyle.copy(color = inputTextColor),
            overflow = TextOverflow.Ellipsis,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        )
    }
}

/**
 * focused 상태의 입력 컴포저블.
 * 빈 텍스트 + placeholder → placeholder 배경 + innerTextField 오버레이, 입력 있음 → innerTextField.
 */
@Composable
private fun ActiveTextContent(
    innerTextField: @Composable () -> Unit,
    text: String,
    placeholder: String?,
    textStyle: TextStyle,
    placeholderColor: Color,
) {
    if (text.isEmpty() && placeholder != null) {
        Text(text = placeholder, style = textStyle, color = placeholderColor)
    }
    // innerTextField()를 호출하지 않으면 커서가 렌더링되지 않는다.
    innerTextField()
}

@Composable
private fun TrailingIcons(
    state: NeveraTextFieldState,
    isActive: Boolean,
    useIcon: Boolean,
    negativeColor: Color,
    trailingIcon: (@Composable () -> Unit)?,
) {
    val showCheckIcon = useIcon && state == NeveraTextFieldState.Positive && isActive
    val showWarningIcon = useIcon && state == NeveraTextFieldState.Negative
    if (!showCheckIcon && !showWarningIcon && trailingIcon == null) return
    val stateIconColor = NeveraTextFieldColors.stateIconColor(state, negativeColor)

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
        trailingIcon?.invoke()
    }
}

/**
 * 포커스 상태에 따라 커서를 자동으로 이동시키는 기본 커서 정책.
 * - 포커스 획득: 커서를 텍스트 맨 뒤로 이동
 * - 포커스 해제: 커서를 맨 앞으로 이동
 *
 * [enabled]=false이면 아무 동작도 하지 않으며, 호출자가 커서를 직접 제어한다.
 */
@Composable
private fun AutoMoveCursorEffect(
    enabled: Boolean,
    isFocused: Boolean,
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
) {
    if (!enabled) return
    // LaunchedEffect는 isFocused 변화 시점의 값을 캡처하므로 rememberUpdatedState로 최신 값을 참조한다.
    val current by rememberUpdatedState(textFieldValue)
    LaunchedEffect(isFocused) {
        val newSelection = if (isFocused) TextRange(current.text.length) else TextRange(0)
        if (current.selection != newSelection) {
            onTextFieldValueChange(current.copy(selection = newSelection))
        }
    }
}
