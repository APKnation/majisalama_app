package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Core ────────────────────────────────────────────────────────────────
// Deep ocean blues — the heart of the Majisalama identity
val MajiPrimary        = Color(0xFF0077B6)   // Deep ocean blue
val MajiPrimaryDark    = Color(0xFF005F99)   // Pressed / hover state
val MajiSecondary      = Color(0xFF00B4D8)   // Bright cyan
val MajiTertiary       = Color(0xFF48CAE4)   // Light aqua

// ── Light Mode ────────────────────────────────────────────────────────────────
val LightBackground         = Color(0xFFF0F8FF)   // Alice blue — soft, clean
val LightSurface            = Color(0xFFFFFFFF)   // Pure white card surface
val LightSurfaceVariant     = Color(0xFFDCEEF7)   // Muted blue-grey panel
val LightSurfaceContainer   = Color(0xFFE8F4FB)   // Subtle container
val LightOnBackground       = Color(0xFF001E2E)   // Near-black, high contrast
val LightOnSurface          = Color(0xFF012A3A)   // Deep navy text
val LightOnSurfaceVariant   = Color(0xFF3D6175)   // Secondary text
val LightOutline            = Color(0xFF8FBDD3)   // Subtle border
val LightOutlineVariant     = Color(0xFFB8D8E8)   // Very subtle border

val LightPrimaryContainer   = Color(0xFFCBE8FF)   // Chip / badge container
val LightSecondaryContainer = Color(0xFFB8EBF7)
val LightTertiaryContainer  = Color(0xFFCAF0F8)
val LightOnPrimaryContainer = Color(0xFF001D30)
val LightInversePrimary     = Color(0xFF8ECFF3)

// ── Dark Mode ─────────────────────────────────────────────────────────────────
val DarkBackground          = Color(0xFF071520)   // Very deep sea — true dark
val DarkSurface             = Color(0xFF0D2233)   // Elevated surface layer
val DarkSurfaceVariant      = Color(0xFF112E44)   // Card / panel layer
val DarkSurfaceContainer    = Color(0xFF17384F)   // Input / inner container
val DarkOnBackground        = Color(0xFFDFF2FF)   // Bright near-white text
val DarkOnSurface           = Color(0xFFCFE8F7)   // Main body text
val DarkOnSurfaceVariant    = Color(0xFF8FBDD3)   // Secondary / hint text
val DarkOutline             = Color(0xFF2D5A73)   // Subtle border
val DarkOutlineVariant      = Color(0xFF1E4059)   // Very subtle border

val DarkPrimary             = Color(0xFF48CAE4)   // Vibrant aqua on dark
val DarkPrimaryDark         = Color(0xFF2EB8D0)
val DarkSecondary           = Color(0xFF90E0EF)   // Soft light blue
val DarkTertiary            = Color(0xFFADE8F4)   // Pale sky

val DarkPrimaryContainer    = Color(0xFF003F5C)
val DarkSecondaryContainer  = Color(0xFF004D66)
val DarkTertiaryContainer   = Color(0xFF005E7A)
val DarkOnPrimaryContainer  = Color(0xFFB3E8F9)
val DarkInversePrimary      = Color(0xFF0077B6)

// ── Semantic / Status Colors ──────────────────────────────────────────────────
val ColorSuccess    = Color(0xFF2EC4B6)   // Teal-green — "safe" / resolved
val ColorWarning    = Color(0xFFFF9F1C)   // Warm amber — caution / in-progress
val ColorDanger     = Color(0xFFE63946)   // Vivid red — alert / critical
val ColorInfo       = Color(0xFF4895EF)   // Bright blue — informational
val ColorMuted      = Color(0xFF8D99AE)   // Grey — disabled / secondary

// ── Gradient Helpers ─────────────────────────────────────────────────────────
// Use these as brush start/end in hero sections
val GradientOceanStart  = Color(0xFF023E8A)   // Deep navy
val GradientOceanEnd    = Color(0xFF0096C7)   // Mid ocean

// ── Backwards-compatible aliases (used in status/priority badges) ─────────────
val WaterAqua    = ColorSuccess
val WaterWarning = ColorWarning
val WaterAlert   = ColorDanger
val WaterMuted   = ColorMuted
val OceanPrimary       = MajiPrimary
val OceanSecondary     = MajiSecondary
val OceanTertiary      = MajiTertiary
val OceanDarkPrimary   = DarkPrimary
val OceanDarkSecondary = DarkSecondary
val OceanDarkTertiary  = DarkTertiary