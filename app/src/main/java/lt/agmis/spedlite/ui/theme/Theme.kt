package lt.agmis.spedlite.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import lt.agmis.spedlite.R

@OptIn(ExperimentalTextApi::class)
val openSans = FontFamily(
    Font(
        resId = R.font.open_sans,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        loadingStrategy = FontLoadingStrategy.Blocking,
        variationSettings = FontVariation.Settings(FontWeight.Normal, FontStyle.Normal),
    ),
    Font(
        resId = R.font.open_sans,
        weight = FontWeight.W700,
        style = FontStyle.Normal,
        loadingStrategy = FontLoadingStrategy.Blocking,
        variationSettings = FontVariation.Settings(FontWeight.W700, FontStyle.Normal),
    ),
    Font(
        resId = R.font.open_sans,
        weight = FontWeight.W900,
        style = FontStyle.Normal,
        loadingStrategy = FontLoadingStrategy.Blocking,
        variationSettings = FontVariation.Settings(FontWeight.W900, FontStyle.Normal),
    ),
    Font(
        resId = R.font.open_sans_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
        loadingStrategy = FontLoadingStrategy.Blocking,
        variationSettings = FontVariation.Settings(FontWeight.Normal, FontStyle.Italic),
    )
)

val primary: Color = Color(0xFF1755F5)
val onPrimary: Color = Color(0xFFFFFFFF)
val error: Color = Color(0xFFFF5E57)

private val SpedliteDarkColorScheme = Colors(
    onBackgroundVariant = Color(0xFFB1B2B4),
)
private val DarkColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    background = Color(0xFF323B42),
    onBackground = Color.White,
    surface = Color(0xFF444F57),
    surfaceContainer = Color(0xFF444F57),
    surfaceContainerLow = Color(0xFF444F57),
    surfaceContainerHigh = Color(0xFF444F57),
    surfaceContainerLowest = Color(0xFF444F57),
    surfaceContainerHighest = Color(0xFF444F57),
    onSurface = Color.White,
    error = error,
    onError = Color.White,
    surfaceVariant = Color(0xFF55616A),
    onSurfaceVariant = Color(0xFFC0C4C9)
)

private val SpedliteLightColorScheme = Colors(
    onBackgroundVariant = Color(0xFF535353)
)
private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    background = Color(0xFFF0F4F9),
    onBackground = Color(0xFF323B42),
    surface = Color.White,
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFFFFFFF),
    onSurface = Color(0xFF323B42),
    error = error,
    onError = Color.White,
    surfaceVariant = Color(0xFFF1F6FB),
    onSurfaceVariant = Color(0xFF9D9D9D)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = openSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = openSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = openSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = openSans,
        fontWeight = FontWeight.W700,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = openSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    )
)

@Composable
fun SpedliteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val spedliteColors = when {
        darkTheme -> SpedliteDarkColorScheme
        else -> SpedliteLightColorScheme
    }
    if (!LocalInspectionMode.current) {
        val insetsController = rememberWindowInsetsControllerCompat()
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
    CompositionLocalProvider(
        LocalDimensions provides Dimensions(),
        LocalColors provides spedliteColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            shapes = Shapes()
        )
    }
}

private fun Context.unwrapAsActivityContext(): Activity {
    fun unwrap(context: Context): Activity {
        when (context) {
            is Activity -> return context
            is ContextWrapper -> {
                return unwrap(context.baseContext)
            }

            else -> {
                throw IllegalStateException("Can't unwrap context to activity context")
            }
        }
    }
    return unwrap(this)
}

@Composable
fun rememberWindowInsetsControllerCompat(): WindowInsetsControllerCompat {
    val view = LocalView.current
    val window = remember(view) {
        if (view is DialogWindowProvider) {
            view.window
        } else {
            view.context.unwrapAsActivityContext().window
        }
    }
    return remember(view, window) {
        WindowCompat.getInsetsController(window, view)
    }
}