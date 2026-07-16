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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour Tokens ───────────────────────────────────────────────────────────
val MBlack      = Color(0xFF0A0A0A)
val MDarkGray   = Color(0xFF141414)
val MSurface    = Color(0xFF1C1C1E)   // iOS-like dark surface
val MBorderGray = Color(0xFF2C2C2E)
val MTextWhite  = Color(0xFFFFFFFF)
val MTextMuted  = Color(0xFF8E8E93)

// BMW M Tricolor
val MBlueLight  = Color(0xFF0A84FF)   // iOS-blue variant for legibility
val MBlueDark   = Color(0xFF1C69D4)
val MRed        = Color(0xFFE22718)

// Card shape — rounded for mobile
val CardShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(6.dp)
val ButtonShape = RoundedCornerShape(8.dp)

// ── Stripe Divider (BMW M) ──────────────────────────────────────────────────
@Composable
fun MStripesDivider(
    modifier: Modifier = Modifier,
    height: Dp = 3.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(2.dp))
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MBlueLight))
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MBlueDark))
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MRed))
    }
}

// ── Card ────────────────────────────────────────────────────────────────────
@Composable
fun MCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MSurface,
    borderColor: Color = MBorderGray,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(backgroundColor, CardShape)
            .border(1.dp, borderColor, CardShape)
            .padding(16.dp),
        content = content
    )
}

// ── Button ──────────────────────────────────────────────────────────────────
@Composable
fun MButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MBlueDark,
    contentColor: Color = MTextWhite,
    borderColor: Color = Color.Transparent
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (backgroundColor == Color.Transparent) MBlueDark else backgroundColor,
            contentColor = if (enabled) contentColor else MTextMuted,
            disabledContainerColor = MBorderGray.copy(alpha = 0.3f),
            disabledContentColor = MTextMuted
        ),
        enabled = enabled,
        border = if (borderColor != Color.Transparent) {
            androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        } else null,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ── Text Field ──────────────────────────────────────────────────────────────
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
            text = label,
            color = MTextMuted,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MSurface,
                unfocusedContainerColor = MDarkGray,
                disabledContainerColor = MDarkGray,
                focusedTextColor = MTextWhite,
                unfocusedTextColor = MTextWhite,
                disabledTextColor = MTextMuted,
                cursorColor = MBlueLight,
                focusedBorderColor = MBlueLight,
                unfocusedBorderColor = MBorderGray,
                disabledBorderColor = MBorderGray,
                focusedLabelColor = MBlueLight,
                unfocusedLabelColor = MTextMuted
            ),
            singleLine = singleLine,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
        )
    }
}

// ── Priority Badge ───────────────────────────────────────────────────────────
@Composable
fun PriorityBadge(priority: String) {
    val (label, color) = when (priority.lowercase()) {
        "low"      -> Pair("NDOGO", Color(0xFF34C759))
        "medium"   -> Pair("WASTANI", Color(0xFFFF9F0A))
        "high"     -> Pair("KUBWA", Color(0xFFFF6B00))
        "critical" -> Pair("DHARURA", Color(0xFFFF3B30))
        else       -> Pair(priority.uppercase(), MTextMuted)
    }
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

// ── Status Badge ─────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: String) {
    val (label, color) = when (status.lowercase()) {
        "pending_village"         -> Pair("Inasubiri", MBlueLight)
        "village_approved"        -> Pair("Imeidhinishwa", Color(0xFF30D158))
        "forwarded_to_district"   -> Pair("Wilaya", Color(0xFFBF5AF2))
        "rejected"                -> Pair("Imekataliwa", Color(0xFFFF3B30))
        "assigned"                -> Pair("Imepewa", Color(0xFF64D2FF))
        "in_progress"             -> Pair("Inafanywa", Color(0xFFFFD60A))
        "resolved"                -> Pair("Imetatuliwa", Color(0xFF30D158))
        "closed"                  -> Pair("Imefungwa", MTextMuted)
        "safe"                    -> Pair("Salama", Color(0xFF30D158))
        "caution"                 -> Pair("Tahadhari", Color(0xFFFF9F0A))
        "unsafe"                  -> Pair("Hatarini", Color(0xFFFF3B30))
        "under_repair"            -> Pair("Inatengenezwa", Color(0xFFFF6B00))
        "dry"                     -> Pair("Kavu", Color(0xFF8E8E93))
        else                      -> Pair(status.replace("_"," ").uppercase(), MTextMuted)
    }
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
