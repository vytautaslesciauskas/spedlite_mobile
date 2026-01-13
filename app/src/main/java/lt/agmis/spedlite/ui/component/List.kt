package lt.agmis.spedlite.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SpedliteListItem(
    onClick: () -> Unit,
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    SpedliteCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingContent != null) {
                leadingContent.invoke()
                Gap()
            }
            Column(modifier = Modifier.weight(1f)) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                    headlineContent()
                }
                if (supportingContent != null) {
                    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB1B2B4))) {
                        supportingContent.invoke()
                    }
                }
            }
            if (trailingContent != null) {
                Gap()
                trailingContent.invoke()
            }
        }
    }
}