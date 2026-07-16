package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary                = DarkPrimary,
    onPrimary              = DarkBackground,
    primaryContainer       = DarkPrimaryContainer,
    onPrimaryContainer     = DarkOnPrimaryContainer,
    inversePrimary         = DarkInversePrimary,

    secondary              = DarkSecondary,
    onSecondary            = DarkBackground,
    secondaryContainer     = DarkSecondaryContainer,
    onSecondaryContainer   = DarkOnPrimaryContainer,

    tertiary               = DarkTertiary,
    onTertiary             = DarkBackground,
    tertiaryContainer      = DarkTertiaryContainer,
    onTertiaryContainer    = DarkOnPrimaryContainer,

    background             = DarkBackground,
    onBackground           = DarkOnBackground,

    surface                = DarkSurface,
    onSurface              = DarkOnSurface,
    surfaceVariant         = DarkSurfaceVariant,
    onSurfaceVariant       = DarkOnSurfaceVariant,
    surfaceContainer       = DarkSurfaceContainer,

    outline                = DarkOutline,
    outlineVariant         = DarkOutlineVariant,

    error                  = ColorDanger,
    onError                = DarkBackground
)

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary                = MajiPrimary,
    onPrimary              = LightBackground,
    primaryContainer       = LightPrimaryContainer,
    onPrimaryContainer     = LightOnPrimaryContainer,
    inversePrimary         = LightInversePrimary,

    secondary              = MajiSecondary,
    onSecondary            = LightBackground,
    secondaryContainer     = LightSecondaryContainer,
    onSecondaryContainer   = LightOnPrimaryContainer,

    tertiary               = MajiTertiary,
    onTertiary             = LightBackground,
    tertiaryContainer      = LightTertiaryContainer,
    onTertiaryContainer    = LightOnPrimaryContainer,

    background             = LightBackground,
    onBackground           = LightOnBackground,

    surface                = LightSurface,
    onSurface              = LightOnSurface,
    surfaceVariant         = LightSurfaceVariant,
    onSurfaceVariant       = LightOnSurfaceVariant,
    surfaceContainer       = LightSurfaceContainer,

    outline                = LightOutline,
    outlineVariant         = LightOutlineVariant,

    error                  = ColorDanger,
    onError                = LightBackground
)

// ── Theme Composable ──────────────────────────────────────────────────────────
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled — always use our branded palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Sync the status-bar colour with our surface so it looks native
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MajiTypography,
        shapes      = MajiShapes,
        content     = content
    )
}