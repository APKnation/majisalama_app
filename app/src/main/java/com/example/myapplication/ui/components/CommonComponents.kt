package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

// ── Shape constants (reference values — composables use MaterialTheme.shapes.*) ─
val CardShape   = RoundedCornerShape(32.dp)
val ChipShape   = RoundedCornerShape(8.dp)
val ButtonShape = RoundedCornerShape(16.dp)

// ── Stripe Divider ────────────────────────────────────────────────────────────
/**
 * Soft accent divider — replaces the old racing-stripe aesthetic with a
 * smooth, semi-transparent line that adapts to the current color scheme.
 */
@Composable
fun MStripesDivider(
    modifier: Modifier = Modifier,
    height: Dp = 3.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    )
}

// ── Card ──────────────────────────────────────────────────────────────────────
/**
 * Standard surface card with soft rounded corners and no harsh border.
 */
@Composable
fun MCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.extraLarge,
        colors    = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border    = null
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content  = content
        )
    }
}

// ── Button ────────────────────────────────────────────────────────────────────
/**
 * Primary / ghost button with consistent 56 dp touch target height.
 */
@Composable
fun MButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = OceanPrimary,
    contentColor: Color = Color.White,
    borderColor: Color = Color.Transparent
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(52.dp),
        shape    = MaterialTheme.shapes.medium,
        colors   = ButtonDefaults.buttonColors(
            containerColor        = if (backgroundColor == Color.Transparent) OceanPrimary else backgroundColor,
            contentColor          = if (enabled) contentColor else WaterMuted,
            disabledContainerColor = WaterMuted.copy(alpha = 0.2f),
            disabledContentColor  = WaterMuted
        ),
        enabled  = enabled,
        border   = if (borderColor != Color.Transparent)
            androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 5.dp
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ── Text Field ────────────────────────────────────────────────────────────────
/**
 * Labelled outlined text field matching the design system.
 */
@Composable
fun MTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text  = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        OutlinedTextField(
            value             = value,
            onValueChange     = onValueChange,
            modifier          = Modifier.fillMaxWidth(),
            shape             = MaterialTheme.shapes.small,
            colors            = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor  = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                disabledTextColor       = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor             = MaterialTheme.colorScheme.primary,
                focusedBorderColor      = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                disabledBorderColor     = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            singleLine            = singleLine,
            enabled               = enabled,
            visualTransformation  = visualTransformation,
            keyboardOptions       = keyboardOptions
        )
    }
}

// ── Priority Badge ────────────────────────────────────────────────────────────
@Composable
fun PriorityBadge(priority: String) {
    val (label, color) = when (priority.lowercase()) {
        "low"      -> Pair("NDOGO",   WaterAqua)
        "medium"   -> Pair("WASTANI", WaterWarning)
        "high"     -> Pair("KUBWA",   WaterAlert)
        "critical" -> Pair("DHARURA", WaterAlert)
        else       -> Pair(priority.uppercase(), WaterMuted)
    }
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text          = label,
            color         = color,
            style         = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

// ── Status Badge ──────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: String) {
    val (label, color) = when (status.lowercase()) {
        "pending_village"       -> Pair("Inasubiri",    MajiSecondary)
        "village_approved"      -> Pair("Imeidhinishwa", WaterAqua)
        "forwarded_to_district" -> Pair("Wilaya",        MajiPrimary)
        "rejected"              -> Pair("Imekataliwa",   WaterAlert)
        "assigned"              -> Pair("Imepewa",       MajiTertiary)
        "in_progress"           -> Pair("Inafanywa",     WaterWarning)
        "resolved"              -> Pair("Imetatuliwa",   WaterAqua)
        "closed"                -> Pair("Imefungwa",     WaterMuted)
        "safe"                  -> Pair("Salama",        WaterAqua)
        "caution"               -> Pair("Tahadhari",     WaterWarning)
        "unsafe"                -> Pair("Hatarini",      WaterAlert)
        "under_repair"          -> Pair("Inatengenezwa", WaterWarning)
        "dry"                   -> Pair("Kavu",          WaterMuted)
        else                    -> Pair(status.replace("_", " ").uppercase(), WaterMuted)
    }
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text          = label,
            color         = color,
            style         = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            textAlign     = TextAlign.Center
        )
    }
}
