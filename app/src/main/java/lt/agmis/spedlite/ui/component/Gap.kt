package lt.agmis.spedlite.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import lt.agmis.spedlite.ui.theme.SpedliteTheme

@Composable
@NonRestartableComposable
fun ColumnScope.Gap(height: Dp = SpedliteTheme.dimen.verticalPadding) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
@NonRestartableComposable
fun LazyItemScope.Gap(height: Dp = SpedliteTheme.dimen.verticalPadding) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
@NonRestartableComposable
fun RowScope.Gap(width: Dp = SpedliteTheme.dimen.horizontalPadding) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
@NonRestartableComposable
fun ColumnScope.GapWeight(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
@NonRestartableComposable
fun RowScope.GapWeight(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
@NonRestartableComposable
fun ColumnScope.GapHalf() {
    Gap(SpedliteTheme.dimen.verticalPadding / 2)
}

@Composable
@NonRestartableComposable
fun RowScope.GapHalf() {
    Gap(SpedliteTheme.dimen.horizontalPadding / 2)
}
