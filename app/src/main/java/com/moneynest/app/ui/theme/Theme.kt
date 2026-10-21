
package com.moneynest.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6C4DFF),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFEAE4FF),
    onPrimaryContainer = Color(0xFF29145E),

    secondary = Color(0xFF8B78DC),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFF0ECFF),
    onSecondaryContainer = Color(0xFF342660),

    tertiary = Color(0xFFB58AD8),

    background = Color(0xFFF6F4FF),
    onBackground = Color(0xFF1F2140),

    surface = Color.White,
    onSurface = Color(0xFF1F2140),

    surfaceVariant = Color(0xFFF0EDFA),
    onSurfaceVariant = Color(0xFF68657C),

    outline = Color(0xFFDAD5E8),

    error = Color(0xFFE34B68),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB7A6FF),
    onPrimary = Color(0xFF28145C),

    primaryContainer = Color(0xFF47318B),
    onPrimaryContainer = Color(0xFFEAE4FF),

    secondary = Color(0xFFC5B8FF),
    onSecondary = Color(0xFF30235F),

    background = Color(0xFF121225),
    onBackground = Color(0xFFF2F0FF),

    surface = Color(0xFF1D1D35),
    onSurface = Color(0xFFF2F0FF),

    surfaceVariant = Color(0xFF302E48),
    onSurfaceVariant = Color(0xFFC8C3DC),

    outline = Color(0xFF77718E),

    error = Color(0xFFFF8BA0),
    onError = Color(0xFF600F25)
)

@Composable
fun MoneyNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
