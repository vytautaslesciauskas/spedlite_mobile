package lt.agmis.spedlite.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.agmis.spedlite.ui.theme.SpedliteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpedliteTopAppBar(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = SpedliteTheme.dimen.horizontalPadding)
            .heightIn(min = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpedliteTheme.dimen.horizontalPadding / 2)
    ) {
        if (navigationIcon != null) {
            navigationIcon()
        }
        Box(modifier = Modifier.weight(1f)) {
            if (title != null) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) {
                    title()
                }
            }
        }
        if (actions != null) {
            actions()
        }
    }
}