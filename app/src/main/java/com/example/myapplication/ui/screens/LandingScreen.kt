package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var recentReports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var waterSources by remember { mutableStateOf<List<WaterSource>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            val reportResult = ApiClient.getDamageReports()
            if (reportResult.isSuccess) recentReports = reportResult.getOrThrow()

            val sourceResult = ApiClient.getWaterSources()
            if (sourceResult.isSuccess) waterSources = sourceResult.getOrThrow()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueMist) // Light blue background
    ) {
        // ── Custom Header matching the reference ──────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueAbyss)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = WhitePure,
                modifier = Modifier.size(28.dp).clickable { onNavigateToLogin() }
            )
            Text(
                text = "MajiSalama",
                color = WhitePure,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Login",
                tint = WhitePure,
                modifier = Modifier.size(28.dp).clickable { onNavigateToLogin() }
            )
        }

        // ── Main Scrollable Content ───────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            // ── Hero Banner ───────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlueDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = WhitePure,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Tunza Maji,\nTunza Uhai.",
                            color = WhitePure,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── 2-Column Grid for Actions ─────────────────────────────────────
            item {
                val actions = listOf(
                    ActionItem("Ripoti", "Ripoti uharibifu sasa", Icons.Default.ReportProblem),
                    ActionItem("Vyanzo", "Tafuta chanzo karibu", Icons.Default.LocationOn),
                    ActionItem("AI Predict", "Utabiri wa mahitaji", Icons.Default.AutoAwesome),
                    ActionItem("Ripoti Zangu", "Taarifa za uharibifu", Icons.AutoMirrored.Filled.ListAlt),
                    ActionItem("Maoni", "Toa maoni yako!", Icons.Default.ThumbUp),
                    ActionItem("Mipangilio", "Sanidi mipangilio", Icons.Default.Settings)
                )

                Column {
                    val rows = actions.chunked(2)
                    for (row in rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (action in row) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(110.dp)
                                        .clickable { onNavigateToLogin() },
                                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    border = null
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = action.title,
                                            tint = BlueAbyss,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = action.title,
                                            color = BlueNight,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = action.subtitle,
                                            color = SubtleOnWhite,
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Recent Reports Section ────────────────────────────────────────
            item {
                Text(
                    text = "Ripoti za Hivi Karibuni",
                    color = BlueNight,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (recentReports.isEmpty()) {
                item {
                    Text(
                        text = "Hakuna ripoti bado",
                        color = SubtleOnWhite,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            } else {
                items(recentReports.take(5)) { report -> // Show top 5 recent
                    com.example.myapplication.ui.components.CleanReportCard(
                        report = report,
                        onClick = onNavigateToLogin
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

data class ActionItem(val title: String, val subtitle: String, val icon: ImageVector)
