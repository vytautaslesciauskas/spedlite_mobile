package lt.agmis.spedlite.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lt.agmis.spedlite.R
import lt.agmis.spedlite.ui.theme.SpedliteTheme

@Composable
fun SpedliteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = SpedliteTheme.colorScheme.onBackgroundVariant),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(LocalContentColor.current),
    label: @Composable () -> Unit,
    suffix: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
            CompositionLocalProvider(LocalTextStyle provides textStyle, LocalContentColor provides textStyle.color) {
                val borderColor = if (isFocused) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color(0xFFB8BDC2)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = SpedliteTheme.dimen.gridSize * 5, vertical = SpedliteTheme.dimen.gridSize * 4)
                ) {
                    if (prefix != null) {
                        prefix()
                        Gap2()
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            label()
                        }
                        innerTextField()
                    }
                    if (suffix != null) {
                        Gap2()
                        suffix()
                    }
                }
            }
        }
    )
}

@Composable
fun SpedliteTextFieldPassword(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = SpedliteTheme.colorScheme.onBackgroundVariant),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(LocalContentColor.current),
    label: @Composable () -> Unit,
    prefix: (@Composable () -> Unit)? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    SpedliteTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        label = label,
        suffix = {
            Icon(
                painter = painterResource(if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off),
                contentDescription = null,
                modifier = Modifier.clickable(
                    onClick = {
                        passwordVisible = !passwordVisible
                    },
                    indication = ripple(bounded = false),
                    interactionSource = null
                )
            )
        },
        prefix = prefix
    )
}