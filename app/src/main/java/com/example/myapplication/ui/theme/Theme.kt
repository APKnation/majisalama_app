package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Only using Light Color Scheme for the clean Blue/White look ──────────────
private val LightColorScheme = lightColorScheme(
    primary                = BlueAbyss,
    onPrimary              = WhitePure,
    primaryContainer       = BlueDeep,
    onPrimaryContainer     = WhitePure,
    inversePrimary         = BlueWave,

    secondary              = BlueOcean,
    onSecondary            = WhitePure,
    secondaryContainer     = BlueIce,
    onSecondaryContainer   = BlueAbyss,

    tertiary               = BlueWave,
    onTertiary             = WhitePure,
    tertiaryContainer      = BlueFoam,
    onTertiaryContainer    = BlueNight,

    background             = BlueMist, // Very light blue background
    onBackground           = BlueNight, // Dark blue text

    surface                = WhitePure, // White cards
    onSurface              = BlueAbyss, // Dark blue text on cards
    surfaceVariant         = WhiteSoft, // Slightly off-white for subtle variation
    onSurfaceVariant       = SubtleOnWhite, // Muted blue text
    surfaceContainer       = WhitePure,

    outline                = BlueIce, // Very subtle blue outline
    outlineVariant         = BlueMist,

    error                  = BlueNight, // Keeping semantic colors blue-themed per request
    onError                = WhitePure
)

// Fallback dark scheme (forces light colors anyway to maintain the strict blue/white brand)
private val DarkColorScheme = LightColorScheme

// ── Theme Composable ──────────────────────────────────────────────────────────
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled — always use our branded palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Force light scheme to maintain the clean blue/white look
    val colorScheme = LightColorScheme

    // Sync the status-bar colour with our primary color (deep blue) for the header
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false // White icons on dark blue status bar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MajiTypography,
        shapes      = MajiShapes,
        content     = content
    )
}