package lt.agmis.spedlite.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lt.agmis.spedlite.ui.theme.SpedliteTheme

@Composable
fun SpedliteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            content()
        }
    )
}

@Composable
fun SpedliteButtonError(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.38f),
            disabledContainerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            content()
        }
    )
}

@Composable
fun SpedliteButtonSuccess(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SpedliteTheme.colorScheme.success,
            contentColor = SpedliteTheme.colorScheme.onSuccess,
            disabledContentColor = SpedliteTheme.colorScheme.onSuccess.copy(alpha = 0.38f),
            disabledContainerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            content()
        }
    )
}

@Composable
fun SpedliteTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            content()
        }
    )
}


@Composable
fun SpedliteIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        content = {
            Box(
                modifier = Modifier
                    .sizeIn(40.dp, 40.dp)
                    .padding(PaddingValues(horizontal = 12.dp)), contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
    )
}

@Composable
fun SpedliteIconButtonRound(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        content = {
            Box(modifier = Modifier.padding(PaddingValues(8.dp))) {
                content()
            }
        },
    )
}