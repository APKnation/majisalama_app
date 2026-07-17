package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════
// MAJISALAMA — Blue + White Water Color Palette
// ══════════════════════════════════════════════════════

// Blue spectrum (light → deep ocean)
val BlueMist    = Color(0xFFEBF5FB)   // page background
val BlueIce     = Color(0xFFBBDEFB)   // chip / badge bg
val BlueFoam    = Color(0xFF90CAF9)   // border / subtle
val BlueWave    = Color(0xFF42A5F5)   // accent highlights
val BlueOcean   = Color(0xFF1E88E5)   // icons / active
val BlueDeep    = Color(0xFF1565C0)   // secondary elements
val BlueAbyss   = Color(0xFF0D47A1)   // top bar / buttons
val BlueNight   = Color(0xFF0A3D7C)   // darkest / headers

// White spectrum
val WhitePure   = Color(0xFFFFFFFF)
val WhiteSoft   = Color(0xFFF8FBFF)
val WhiceIce    = Color(0xFFF0F8FF)   // Alice blue

// Text
val OnBlue          = Color(0xFFFFFFFF)   // white text on blue bg
val OnWhite         = Color(0xFF0D47A1)   // blue text on white bg
val SubtleOnWhite   = Color(0xFF5B85AA)   // muted text on white

// Gradient endpoints for splash / hero
val GradientTop    = Color(0xFF1565C0)
val GradientBottom = Color(0xFF0A3D7C)

// Semantic aliases (all blue tones — water-only palette)
val WaterSafe    = BlueOcean
val WaterCaution = BlueDeep
val WaterDanger  = BlueNight
val WaterFixed   = BlueWave
val ButtonAccent = Color(0xFF00B4D8)  // Vibrant Cyan for unique buttons

// ── Backwards-compatible aliases used across existing screens ──────────
val WaterAqua        = WaterSafe
val WaterWarning     = WaterCaution
val WaterAlert       = WaterDanger
val WaterMuted       = BlueFoam
val OceanPrimary     = BlueAbyss
val OceanSecondary   = BlueOcean
val OceanTertiary    = BlueWave
val MajiPrimary      = BlueAbyss
val MajiSecondary    = BlueOcean
val MajiTertiary     = BlueWave
val ColorDanger      = BlueNight
val ColorWarning     = BlueDeep
val ColorSuccess     = BlueOcean
val ColorInfo        = BlueWave
val ColorMuted       = BlueFoam
val GradientOceanStart = GradientBottom
val GradientOceanEnd   = GradientTop

// Dark aliases (kept for backwards compat, not used in light theme)
val DarkBackground       = BlueNight
val DarkSurface          = Color(0xFF0D2746)
val DarkSurfaceVariant   = Color(0xFF112E5A)
val DarkSurfaceContainer = Color(0xFF17385E)
val DarkOnBackground     = WhitePure
val DarkOnSurface        = Color(0xFFDCEEFF)
val DarkOnSurfaceVariant = BlueFoam
val DarkOutline          = Color(0xFF2A4D80)
val DarkOutlineVariant   = Color(0xFF1E3D6B)
val DarkPrimary          = BlueWave
val DarkPrimaryDark      = BlueOcean
val DarkSecondary        = BlueFoam
val DarkTertiary         = BlueIce
val DarkPrimaryContainer    = Color(0xFF003F6B)
val DarkSecondaryContainer  = Color(0xFF004D80)
val DarkTertiaryContainer   = Color(0xFF005E9A)
val DarkOnPrimaryContainer  = Color(0xFFB3D8FF)
val DarkInversePrimary      = BlueAbyss
val LightBackground         = WhiceIce
val LightSurface            = WhitePure
val LightSurfaceVariant     = BlueMist
val LightSurfaceContainer   = WhiteSoft
val LightOnBackground       = OnWhite
val LightOnSurface          = OnWhite
val LightOnSurfaceVariant   = SubtleOnWhite
val LightOutline            = BlueFoam
val LightOutlineVariant     = BlueIce
val LightPrimaryContainer   = BlueIce
val LightSecondaryContainer = BlueMist
val LightTertiaryContainer  = BlueIce
val LightOnPrimaryContainer = BlueNight
val LightInversePrimary     = BlueWave