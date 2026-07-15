package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Tokens
val MBlack = Color(0xFF000000)
val MDarkGray = Color(0xFF0A0A0A)
val MBorderGray = Color(0xFF2C2C2C)
val MTextWhite = Color(0xFFFFFFFF)
val MTextMuted = Color(0xFFBBBBBB)

// BMW M Tricolor Scheme
val MBlueLight = Color(0xFF0066B1)
val MBlueDark = Color(0xFF1C69D4)
val MRed = Color(0xFFE22718)

@Composable
fun MStripesDivider(
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MBlueLight)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MBlueDark)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MRed)
        )
    }
}

@Composable
fun MCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MDarkGray,
    borderColor: Color = MBorderGray,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor, shape = RectangleShape)
            .border(1.dp, borderColor, shape = RectangleShape)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun MButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MTextWhite,
    borderColor: Color = MTextWhite
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .border(
                1.dp,
                if (enabled) borderColor else MBorderGray,
                shape = RectangleShape
            ),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = if (enabled) contentColor else MTextMuted,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MTextMuted
        ),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

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
            text = label.uppercase(),
            color = MTextMuted,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MBorderGray, RectangleShape),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MDarkGray,
                unfocusedContainerColor = MBlack,
                disabledContainerColor = MBlack,
                focusedTextColor = MTextWhite,
                unfocusedTextColor = MTextWhite,
                disabledTextColor = MTextMuted,
                cursorColor = MTextWhite,
                focusedIndicatorColor = MTextWhite,
                unfocusedIndicatorColor = MBorderGray,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RectangleShape,
            singleLine = singleLine,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (SwahiliText, color) = when (priority.lowercase()) {
        "low" -> Pair("NDOGO", Color(0xFF4CAF50))
        "medium" -> Pair("WASTANI", Color(0xFFFFC107))
        "high" -> Pair("KUBWA", Color(0xFFFF9800))
        "critical" -> Pair("DHARURA", Color(0xFFF44336))
        else -> Pair(priority.uppercase(), MTextMuted)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), shape = RectangleShape)
            .border(1.dp, color, shape = RectangleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = SwahiliText,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (SwahiliText, color) = when (status.lowercase()) {
        "pending_village" -> Pair("INASUBIRI IDHINI YA MWENYEKITI", MBlueLight)
        "village_approved" -> Pair("IMEIDHINISHWA NA MWENYEKITI", MBlueDark)
        "forwarded_to_district" -> Pair("IMETUMWA KWA WILAYA", Color(0xFF9C27B0))
        "rejected" -> Pair("IMEKATALIWA", Color(0xFFF44336))
        "assigned" -> Pair("IMEPEWA WAFANYAKAZI", Color(0xFF00BCD4))
        "in_progress" -> Pair("INAFANYWA KAZI", Color(0xFFFFEB3B))
        "resolved" -> Pair("IMETATULIWA", Color(0xFF4CAF50))
        "closed" -> Pair("IMEFUNGWA", Color(0xFF9E9E9E))
        "safe" -> Pair("SALAMA", Color(0xFF4CAF50))
        "caution" -> Pair("TAHADHARI", Color(0xFFFFC107))
        "unsafe" -> Pair("HATARINI", Color(0xFFF44336))
        "under_repair" -> Pair("INATENGENEZWA", Color(0xFFFF9800))
        "dry" -> Pair("KAVU", Color(0xFF795548))
        else -> Pair(status.uppercase(), MTextMuted)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), shape = RectangleShape)
            .border(1.dp, color, shape = RectangleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = SwahiliText,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}
