package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.Alert
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSourceDetails: (Int) -> Unit,
    onNavigateToReportDamage: (Int?) -> Unit,
    onNavigateToPredictor: () -> Unit
) {
    var searchQueries by remember { mutableStateOf("") }
    var sources by remember { mutableStateOf<List<WaterSource>>(emptyList()) }
    var alerts by remember { mutableStateOf<List<Alert>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Nearby Filter States
    var useNearbyFilter by remember { mutableStateOf(false) }
    var latText by remember { mutableStateOf("-6.8") }
    var lngText by remember { mutableStateOf("39.28") }
    var radiusText by remember { mutableStateOf("5") }

    val user = ApiClient.currentUser

    // Refresh function
    val loadDashboardData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val sourcesResult = if (useNearbyFilter) {
                    val lat = latText.toDoubleOrNull() ?: -6.8
                    val lng = lngText.toDoubleOrNull() ?: 39.28
                    val radius = radiusText.toDoubleOrNull() ?: 5.0
                    ApiClient.getNearbyWaterSources(lat, lng, radius)
                } else {
                    ApiClient.getWaterSources()
                }

                if (sourcesResult.isSuccess) {
                    sources = sourcesResult.getOrThrow()
                } else {
                    errorMessage = sourcesResult.exceptionOrNull()?.message
                }

                // If authenticated, load alerts
                if (ApiClient.accessToken != null) {
                    val alertsResult = ApiClient.getAlerts()
                    if (alertsResult.isSuccess) {
                        alerts = alertsResult.getOrThrow()
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(useNearbyFilter) {
        loadDashboardData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Welcome Header ────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HABARI, ${user?.displayName?.uppercase() ?: "MGENI"}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Role: ${user?.role?.uppercase() ?: "PUBLIC CITIZEN"}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                MButton(text = "REFRESH", onClick = { loadDashboardData() })
            }
        }

        item { MStripesDivider() }

        // ── Quick Action Shortcuts ─────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MButton(
                    text = "PREDICT DEMAND (AI)",
                    onClick = onNavigateToPredictor,
                    modifier = Modifier.weight(1f)
                )
                MButton(
                    text = "RIPOTI UHARIBIFU",
                    onClick = { onNavigateToReportDamage(null) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Alerts Banner ─────────────────────────────────────────────────
        if (alerts.isNotEmpty()) {
            item {
                MCard(borderColor = MaterialTheme.colorScheme.error, backgroundColor = Color(0xFF1A0A0A)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "ARIFA KUU / TANGAZO LA MAJI",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    alerts.take(3).forEach { alert ->
                        Text(
                            text = "• [${alert.alertTypeDisplay.uppercase()}] ${alert.message}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // ── Search Bar & Filter ───────────────────────────────────────────
        item {
            MCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    BasicTextField(
                        value = searchQueries,
                        onValueChange = { searchQueries = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        decorationBox = { innerTextField ->
                            if (searchQueries.isEmpty()) {
                                Text("Tafuta chanzo cha maji au kijiji...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { useNearbyFilter = !useNearbyFilter }
                ) {
                    Checkbox(
                        checked = useNearbyFilter,
                        onCheckedChange = { useNearbyFilter = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onSurface,
                            checkmarkColor = MaterialTheme.colorScheme.background,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Text(
                        text = "TAFUTA VILIVYO KARIBU (NEARBY FILTER)",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (useNearbyFilter) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LATITUDE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            BasicTextField(
                                value = latText,
                                onValueChange = { latText = it },
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape).background(MaterialTheme.colorScheme.background).padding(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LONGITUDE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            BasicTextField(
                                value = lngText,
                                onValueChange = { lngText = it },
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape).background(MaterialTheme.colorScheme.background).padding(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("RADIUS (KM)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            BasicTextField(
                                value = radiusText,
                                onValueChange = { radiusText = it },
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape).background(MaterialTheme.colorScheme.background).padding(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        MButton(
                            text = "TAFUTA",
                            onClick = { loadDashboardData() },
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }
            }
        }

        // ── Summary Stats ─────────────────────────────────────────────────
        if (sources.isNotEmpty()) {
            item {
                val total = sources.size
                val salama = sources.count { it.status == "safe" }
                val hatarini = sources.count { it.status == "unsafe" }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f).border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("VYANZO", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("$total", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f).border(1.dp, Color(0xFF4CAF50), RectangleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SALAMA", color = Color(0xFF4CAF50), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("$salama", color = Color(0xFF4CAF50), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f).border(1.dp, Color(0xFFF44336), RectangleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HATARINI", color = Color(0xFFF44336), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("$hatarini", color = Color(0xFFF44336), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── Section Header ────────────────────────────────────────────────
        item {
            Text(
                text = "VYANZO VYA MAJI",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // ── Loading / Empty / List ────────────────────────────────────────
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                }
            }
        } else {
            val filteredSources = sources.filter {
                it.name.contains(searchQueries, ignoreCase = true) ||
                        it.villageName.contains(searchQueries, ignoreCase = true) ||
                        it.sourceTypeDisplay.contains(searchQueries, ignoreCase = true)
            }
            if (filteredSources.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hakuna vyanzo vilivyopatikana.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                items(filteredSources) { source ->
                    WaterSourceCard(
                        source = source,
                        onClick = { onNavigateToSourceDetails(source.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WaterSourceCard(
    source: WaterSource,
    onClick: () -> Unit
) {
    MCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.name.uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Aina: ${source.sourceTypeDisplay} | Kijiji: ${source.villageName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Show last tested ph parameter preview
                if (source.phLevel != null) {
                    Text(
                        text = "pH: ${source.phLevel} | Bacteria: ${source.bacteriaCount ?: 0} CFU",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            StatusBadge(status = source.status)
        }
    }
}
