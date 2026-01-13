package lt.agmis.spedlite.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object SpedliteTheme {

    val dimen: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val colorScheme: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
}

@Immutable
data class Dimensions(
    val gridSize: Dp = 4.dp,
) {
    val gap1: Dp
        get() = gridSize * 1
    val gap2: Dp
        get() = gridSize * 2
    val gap3: Dp
        get() = gridSize * 3
    val gap4: Dp
        get() = gridSize * 4
    val gap5: Dp
        get() = gridSize * 5
    val gap6: Dp
        get() = gridSize * 6
    val gap7: Dp
        get() = gridSize * 7
    val gap8: Dp
        get() = gridSize * 8
    val gap9: Dp
        get() = gridSize * 9
    val gap10: Dp
        get() = gridSize * 10
}

@Immutable
data class Colors(
    val onBackgroundVariant: Color = Color(0xFF535353),
    val success: Color = Color(0xFF00B97F),
    val onSuccess: Color = Color.White,
)

internal val LocalDimensions = staticCompositionLocalOf { Dimensions() }
internal val LocalColors = staticCompositionLocalOf { Colors() }


@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Day", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Night", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewDayNight