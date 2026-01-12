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
    val horizontalPadding: Dp = 20.dp,
    val verticalPadding: Dp = 32.dp,
)

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