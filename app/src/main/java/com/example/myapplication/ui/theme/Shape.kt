package com.example.myapplication.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Majisalama Shape System
 *
 * Consistent rounded corners across the whole app, wired into MaterialTheme.shapes.
 * Use via MaterialTheme.shapes.* in composables — never hardcode corner radii.
 *
 *  extraSmall  →  8 dp   Chips, small tags, tiny badges
 *  small       → 12 dp   Input fields (OutlinedTextField)
 *  medium      → 16 dp   Buttons, standard cards, dialogs
 *  large       → 24 dp   Modals, sheet cards, nav drawer items
 *  extraLarge  → 32 dp   Full-width hero cards, bottom sheets
 */
val MajiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
