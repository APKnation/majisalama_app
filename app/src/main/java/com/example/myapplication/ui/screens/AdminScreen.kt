package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    onNavigateToCitizen: () -> Unit,
    onNavigateToLeader: () -> Unit,
    onNavigateToOfficer: () -> Unit,
    onNavigateToDistrict: () -> Unit,
    onNavigateToPredictor: () -> Unit
) {
    val user = ApiClient.currentUser
    val scope = rememberCoroutineScope()

    var totalReports by remember { mutableStateOf(0) }
    var pendingReports by remember { mutableStateOf(0) }
    var resolvedReports by remember { mutableStateOf(0) }
    var totalSources by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            val rpt = ApiClient.getDamageReports()
            if (rpt.isSuccess) {
                val list = rpt.getOrThrow()
                totalReports  = list.size
                pendingReports  = list.count { it.status.contains("pending") }
                resolvedReports = list.count { it.status == "resolved" || it.status == "closed" }
            }
            val src = ApiClient.getWaterSources()
            if (src.isSuccess) totalSources = src.getOrThrow().size
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueMist),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Hero Header ───────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueAbyss)
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(BlueDeep),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = WhitePure,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Karibu, ${user?.displayName ?: "Admin"}",
                                color = WhitePure,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Admin Panel • MajiSalama",
                                color = WhitePure.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // ── Stat Cards Strip ─────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-16).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalReports.toString(),
                    label = "Ripoti\nZote",
                    icon = Icons.Default.ListAlt,
                    iconTint = BlueOcean
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = pendingReports.toString(),
                    label = "Zinasubiri\nIdhini",
                    icon = Icons.Default.HourglassTop,
                    iconTint = BlueDeep
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = resolvedReports.toString(),
                    label = "Zimetatuliwa",
                    icon = Icons.Default.CheckCircle,
                    iconTint = BlueAbyss
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalSources.toString(),
                    label = "Vyanzo\nvya Maji",
                    icon = Icons.Default.WaterDrop,
                    iconTint = BlueOcean
                )
            }
        }

        // ── Section Title ─────────────────────────────────────────────────────
        item {
            Text(
                text = "Paneli za Watumiaji",
                color = BlueNight,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // ── Navigation Grid 2×2 ───────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AdminNavTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.People,
                        title = "Mwananchi",
                        subtitle = "Dashboard ya Mwananchi",
                        accent = BlueOcean,
                        onClick = onNavigateToCitizen
                    )
                    AdminNavTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalance,
                        title = "Kiongozi",
                        subtitle = "Dashboard ya Kijiji",
                        accent = BlueDeep,
                        onClick = onNavigateToLeader
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AdminNavTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Engineering,
                        title = "Afisa Maji",
                        subtitle = "Dashboard ya Afisa",
                        accent = BlueAbyss,
                        onClick = onNavigateToOfficer
                    )
                    AdminNavTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocationCity,
                        title = "Wilaya",
                        subtitle = "Dashboard ya Wilaya",
                        accent = BlueNight,
                        onClick = onNavigateToDistrict
                    )
                }
            }
        }

        // ── Divider ───────────────────────────────────────────────────────────
        item { Spacer(Modifier.height(20.dp)) }

        // ── AI Predictor Full-Width Tile ──────────────────────────────────────
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable(onClick = onNavigateToPredictor),
                colors = CardDefaults.cardColors(containerColor = BlueAbyss),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BlueDeep),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = WhitePure,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "AI Predictor",
                                color = WhitePure,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Utabiri wa mahitaji ya maji",
                                color = WhitePure.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = WhitePure.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// ── Admin Stat Card ────────────────────────────────────────────────────────────
@Composable
private fun AdminStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                color = BlueNight,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                color = SubtleOnWhite,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

// ── Admin Nav Tile ─────────────────────────────────────────────────────────────
@Composable
private fun AdminNavTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = BlueNight,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    color = SubtleOnWhite,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}
