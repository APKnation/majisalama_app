package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    backgroundColor: Color = ButtonAccent,
    contentColor: Color = Color.White,
    borderColor: Color = Color.Transparent
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(52.dp),
        shape    = MaterialTheme.shapes.medium,
        colors   = ButtonDefaults.buttonColors(
            containerColor        = if (backgroundColor == Color.Transparent) ButtonAccent else backgroundColor,
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
            shape             = RoundedCornerShape(8.dp),
            colors            = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = WhitePure,
                unfocusedContainerColor = WhitePure,
                disabledContainerColor  = WhitePure.copy(alpha = 0.5f),
                focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                disabledTextColor       = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor             = ButtonAccent,
                focusedBorderColor      = ButtonAccent,
                unfocusedBorderColor    = Color.LightGray,
                disabledBorderColor     = Color.LightGray.copy(alpha = 0.4f)
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
    val statusLower = status.lowercase().trim()
    val (label, color) = when {
        // Pending states -> Vibrant Amber / Orange
        statusLower == "pending" || statusLower == "pending_village" || statusLower == "inasubiri" ->
            Pair("Inasubiri", Color(0xFFE65100))

        // Approved states -> Bright Teal
        statusLower == "village_approved" || statusLower == "imeidhinishwa" ->
            Pair("Imeidhinishwa", Color(0xFF00897B))

        // Forwarded to district -> Deep Purple / Indigo
        statusLower == "forwarded_to_district" || statusLower == "wilaya" ->
            Pair("Wilayani", Color(0xFF673AB7))

        // Assigned states -> Royal Blue
        statusLower == "assigned" || statusLower == "imepewa" ->
            Pair("Imepewa Afisa", Color(0xFF1976D2))

        // In Progress states -> Cyan / Deep Cyan
        statusLower == "in_progress" || statusLower == "inafanywa" || statusLower == "inafanyiwa" ||
                statusLower == "inatengenezwa" || statusLower == "under_repair" ->
            Pair("Inafanywa Kazi", Color(0xFF0097A7))

        // Resolved states -> Pure Emerald Green
        statusLower == "resolved" || statusLower == "imetatuliwa" ->
            Pair("Imetatuliwa", Color(0xFF2E7D32))

        // Closed states -> Slate Gray
        statusLower == "closed" || statusLower == "imefungwa" ->
            Pair("Imefungwa", Color(0xFF455A64))

        // Rejected states -> Crimson Red
        statusLower == "rejected" || statusLower == "imekataliwa" ->
            Pair("Imekataliwa", Color(0xFFC62828))

        // Water quality / source status
        statusLower == "safe" || statusLower == "salama" ->
            Pair("Salama", Color(0xFF2E7D32))
        statusLower == "caution" || statusLower == "tahadhari" ->
            Pair("Tahadhari", Color(0xFFF57C00))
        statusLower == "unsafe" || statusLower == "hatarini" ->
            Pair("Hatarini", Color(0xFFC62828))
        statusLower == "dry" || statusLower == "kavu" ->
            Pair("Kavu", Color(0xFF616161))

        else -> Pair(status.replace("_", " ").uppercase(), Color(0xFF546E7A))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
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

// ── Clean Report Card ─────────────────────────────────────────────────────────
@Composable
fun CleanReportCard(
    report: com.example.myapplication.data.DamageReport,
    onClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = report.waterSourceName.uppercase(),
                    color = BlueOcean,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = report.reportDate,
                    color = SubtleOnWhite,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = report.title,
                color = BlueNight,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = report.description,
                color = SubtleOnWhite,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = report.status)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Person,
                        contentDescription = null,
                        tint = SubtleOnWhite,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    val reporterDisplayName = if (report.reportedByName.isBlank() || report.reportedByName.equals("Anonymous", ignoreCase = true)) {
                        "Mwananchi"
                    } else {
                        report.reportedByName
                    }
                    Text(
                        text = reporterDisplayName,
                        color = SubtleOnWhite,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            actions()
        }
    }
}

// ── SweetAlert Dialog Component ───────────────────────────────────────────────
enum class SweetAlertType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    CONFIRM
}

data class SweetAlertData(
    val title: String,
    val message: String,
    val type: SweetAlertType = SweetAlertType.INFO,
    val confirmButtonText: String = "Sawa",
    val cancelButtonText: String? = null,
    val onConfirm: (() -> Unit)? = null,
    val onCancel: (() -> Unit)? = null
)

private data class AlertVisuals(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val bg: Color
)

@Composable
fun SweetAlertDialog(
    data: SweetAlertData,
    onDismissRequest: () -> Unit
) {
    val visuals = when (data.type) {
        SweetAlertType.SUCCESS -> AlertVisuals(Icons.Default.CheckCircle, Color(0xFF2E7D32), Color(0xFFE8F5E9))
        SweetAlertType.WARNING -> AlertVisuals(Icons.Default.Warning, Color(0xFFEF6C00), Color(0xFFFFF3E0))
        SweetAlertType.ERROR   -> AlertVisuals(Icons.Default.Error, Color(0xFFC62828), Color(0xFFFFEBEE))
        SweetAlertType.INFO    -> AlertVisuals(Icons.Default.Info, Color(0xFF1E88E5), Color(0xFFE3F2FD))
        SweetAlertType.CONFIRM -> AlertVisuals(Icons.Default.Help, Color(0xFF0097A7), Color(0xFFE0F7FA))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        containerColor = WhitePure,
        titleContentColor = BlueNight,
        textContentColor = SubtleOnWhite,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(visuals.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visuals.icon,
                    contentDescription = null,
                    tint = visuals.color,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(
                text = data.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = data.message,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    data.onConfirm?.invoke()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = visuals.color),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = data.confirmButtonText,
                    color = WhitePure,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = if (data.cancelButtonText != null) {
            {
                OutlinedButton(
                    onClick = {
                        data.onCancel?.invoke()
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = data.cancelButtonText,
                        color = SubtleOnWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else null
    )
}
