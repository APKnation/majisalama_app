package com.example.myapplication.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

// ── Filter tabs for recent reports ────────────────────────────────────────────
private val reportFilters = listOf("Zote", "Inasubiri", "Imeidhinishwa", "Dharura")

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Zote") }
    var recentReports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var waterSources by remember { mutableStateOf<List<WaterSource>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load public data on first compose
    LaunchedEffect(Unit) {
        scope.launch {
            val reportResult = ApiClient.getDamageReports()
            if (reportResult.isSuccess) recentReports = reportResult.getOrThrow()

            val sourceResult = ApiClient.getWaterSources()
            if (sourceResult.isSuccess) waterSources = sourceResult.getOrThrow()

            isLoading = false
        }
    }

    // Filter reports
    val filteredReports = recentReports.filter { report ->
        val matchesFilter = when (selectedFilter) {
            "Inasubiri" -> report.status == "pending_village"
            "Imeidhinishwa" -> report.status == "village_approved" || report.status == "assigned"
            "Dharura" -> report.priority == "critical" || report.priority == "high"
            else -> true
        }
        val matchesSearch = searchQuery.isEmpty() ||
            report.title.contains(searchQuery, ignoreCase = true) ||
            report.waterSourceName.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // ── Hero Header ───────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0D2233),
                                DarkBackground
                            )
                        )
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp)
            ) {
                Column {
                    // Greeting row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Karibu Tanzania 🇹🇿",
                                color = DarkOnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Gundua Maji\nSalama Nawe!",
                                color = DarkOnBackground,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 36.sp
                                )
                            )
                        }
                        // Notification bell
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant)
                                .clickable { onNavigateToLogin() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Arifa",
                                tint = DarkPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // Search bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tafuta",
                            tint = DarkOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkOnSurface
                            ),
                            cursorBrush = SolidColor(DarkPrimary),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "Tafuta ripoti au chanzo cha maji...",
                                        color = DarkOnSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Futa",
                                tint = DarkOnSurfaceVariant,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { searchQuery = "" }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sauti",
                                tint = DarkPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Quick Action Categories ───────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        QuickActionItem(
                            icon = Icons.Default.ReportProblem,
                            label = "Ripoti",
                            color = ColorDanger,
                            onClick = onNavigateToLogin
                        )
                    }
                    item {
                        QuickActionItem(
                            icon = Icons.Default.WaterDrop,
                            label = "Vyanzo",
                            color = DarkPrimary,
                            onClick = onNavigateToLogin
                        )
                    }
                    item {
                        QuickActionItem(
                            icon = Icons.Default.AutoAwesome,
                            label = "AI Predict",
                            color = Color(0xFF30D158),
                            onClick = onNavigateToLogin
                        )
                    }
                    item {
                        QuickActionItem(
                            icon = Icons.Default.Science,
                            label = "Ubora",
                            color = ColorWarning,
                            onClick = onNavigateToLogin
                        )
                    }
                    item {
                        QuickActionItem(
                            icon = Icons.Default.People,
                            label = "Wafanyakazi",
                            color = ColorInfo,
                            onClick = onNavigateToLogin
                        )
                    }
                }
            }
        }

        // ── Stats Strip ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    StatChip(
                        value = "${waterSources.size}",
                        label = "Vyanzo",
                        color = DarkPrimary
                    )
                }
                item {
                    StatChip(
                        value = "${waterSources.count { it.status == "safe" }}",
                        label = "Salama",
                        color = ColorSuccess
                    )
                }
                item {
                    StatChip(
                        value = "${waterSources.count { it.status == "unsafe" }}",
                        label = "Hatarini",
                        color = ColorDanger
                    )
                }
                item {
                    StatChip(
                        value = "${recentReports.size}",
                        label = "Ripoti",
                        color = ColorWarning
                    )
                }
            }
        }

        // ── Recent Reports Section Header ─────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ripoti za Hivi Karibuni",
                    color = DarkOnBackground,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Angalia zote →",
                    color = DarkPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Filter Chips ──────────────────────────────────────────────────────
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reportFilters) { filter ->
                    FilterChipItem(
                        label = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // ── Report Cards ──────────────────────────────────────────────────────
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkPrimary)
                }
            }
        } else if (filteredReports.isEmpty()) {
            item {
                EmptyReportsState(onNavigateToLogin)
            }
        } else {
            // 2-column grid of report cards
            val chunked = filteredReports.chunked(2)
            items(chunked) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportCard(
                        report = pair[0],
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLogin
                    )
                    if (pair.size > 1) {
                        ReportCard(
                            report = pair[1],
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToLogin
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        // ── CTA / Auth Section ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientOceanStart, GradientOceanEnd)
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Jiunge na Mfumo",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "Simamia maji, ripoti uharibifu na ufuatilie hali ya vyanzo vyote Tanzania.",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = GradientOceanStart
                        )
                    ) {
                        Text(
                            "Jiandikishe",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    OutlinedButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            "Ingia",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // ── Roles Section ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Watumiaji wa Mfumo",
                color = DarkOnBackground,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoleItem("Mwananchi",       "Angalia hali ya maji & ripoti uharibifu",   Icons.Default.Person,       DarkOnSurface)
                RoleItem("Kiongozi wa Kijiji", "Simamia & idhinisha ripoti za kijiji",   Icons.Default.Groups,       DarkPrimary)
                RoleItem("Afisa wa Maji",   "Tekeleza kazi na ukaguzi wa ubora",          Icons.Default.Engineering,  DarkSecondary)
                RoleItem("Afisa wa Wilaya", "Simamia ripoti zilizopandishwa wilaya",      Icons.Default.LocationCity, ColorDanger)
                RoleItem("Msimamizi",       "Udhibiti kamili wa mfumo wote",             Icons.Default.AdminPanelSettings, ColorWarning)
            }
        }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = DarkOnSurface,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun StatChip(value: String, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = value,
            color = color,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
        )
        Text(
            text = label,
            color = DarkOnSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) DarkPrimary else DarkSurfaceVariant,
        animationSpec = tween(200),
        label = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else DarkOnSurfaceVariant,
        animationSpec = tween(200),
        label = "chipText"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun ReportCard(report: DamageReport, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val priorityColor = when (report.priority.lowercase()) {
        "critical" -> ColorDanger
        "high"     -> Color(0xFFFF6B35)
        "medium"   -> ColorWarning
        else       -> ColorInfo
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .clickable(onClick = onClick)
    ) {
        // Coloured priority bar at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(priorityColor, priorityColor.copy(alpha = 0.3f))
                    )
                )
        )
        Column(modifier = Modifier.padding(12.dp)) {
            // Source name chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = report.waterSourceName.uppercase(),
                    color = DarkPrimary,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = report.title,
                color = DarkOnSurface,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = report.description,
                color = DarkOnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = report.status)
                Text(
                    text = report.reportDate,
                    color = DarkOnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = DarkOnSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = report.reportedByName,
                    color = DarkOnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyReportsState(onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReportProblem,
                contentDescription = null,
                tint = DarkOnSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Hakuna ripoti bado",
            color = DarkOnSurface,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "Kuwa wa kwanza kuripoti uharibifu\nwa maji katika eneo lako.",
            color = DarkOnSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )
        Button(
            onClick = onNavigateToLogin,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Ripoti Uharibifu", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun RoleItem(role: String, desc: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.07f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = role,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = role,
                color = color,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = desc,
                color = DarkOnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = color.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}
