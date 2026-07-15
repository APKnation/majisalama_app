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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack)
            .padding(16.dp)
    ) {
        // Top Welcome & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HABARI, ${user?.displayName?.uppercase() ?: "MGENI"}",
                    color = MTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Role: ${user?.role?.uppercase() ?: "PUBLIC citizen"}",
                    color = MBlueLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            MButton(
                text = "REFRESH",
                onClick = { loadDashboardData() }
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))



        // Global shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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

        // Search Bar & Filter options
        MCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MTextMuted,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BasicTextField(
                    value = searchQueries,
                    onValueChange = { searchQueries = it },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MTextWhite),
                    decorationBox = { innerTextField ->
                        if (searchQueries.isEmpty()) {
                            Text("Tafuta chanzo cha maji au kijiji...", color = MTextMuted, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nearby coordinates search box toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { useNearbyFilter = !useNearbyFilter }
            ) {
                Checkbox(
                    checked = useNearbyFilter,
                    onCheckedChange = { useNearbyFilter = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MTextWhite,
                        checkmarkColor = MBlack,
                        uncheckedColor = MBorderGray
                    )
                )
                Text(
                    text = "TAFUTA VILIVYO KARIBU (NEARBY FILTER)",
                    color = MTextWhite,
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
                        Text("LATITUDE", color = MTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        BasicTextField(
                            value = latText,
                            onValueChange = { latText = it },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = MTextWhite),
                            modifier = Modifier
                                .border(1.dp, MBorderGray, RectangleShape)
                                .background(MBlack)
                                .padding(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("LONGITUDE", color = MTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        BasicTextField(
                            value = lngText,
                            onValueChange = { lngText = it },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = MTextWhite),
                            modifier = Modifier
                                .border(1.dp, MBorderGray, RectangleShape)
                                .background(MBlack)
                                .padding(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RADIUS (KM)", color = MTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        BasicTextField(
                            value = radiusText,
                            onValueChange = { radiusText = it },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = MTextWhite),
                            modifier = Modifier
                                .border(1.dp, MBorderGray, RectangleShape)
                                .background(MBlack)
                                .padding(8.dp),
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

        // Summary Statistics Box
        if (sources.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val total = sources.size
                val salama = sources.count { it.status == "safe" }
                val hatarini = sources.count { it.status == "unsafe" }
                val nyingine = total - salama - hatarini

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MBorderGray, RectangleShape)
                        .background(MDarkGray)
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("VYANZO", color = MTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("$total", color = MTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF4CAF50), RectangleShape)
                        .background(MDarkGray)
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("SALAMA", color = Color(0xFF4CAF50), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("$salama", color = Color(0xFF4CAF50), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFFF44336), RectangleShape)
                        .background(MDarkGray)
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("HATARINI", color = Color(0xFFF44336), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("$hatarini", color = Color(0xFFF44336), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Main List Content
        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MTextWhite)
            }
        } else {
            val filteredSources = sources.filter {
                it.name.contains(searchQueries, ignoreCase = true) ||
                        it.villageName.contains(searchQueries, ignoreCase = true) ||
                        it.sourceTypeDisplay.contains(searchQueries, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show alerts warnings if they exist
                if (alerts.isNotEmpty()) {
                    item {
                        MCard(
                            borderColor = MRed,
                            backgroundColor = Color(0xFF1A0A0A)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = MRed,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "ARIFA KUU / TANGAZO LA MAJI",
                                    color = MTextWhite,
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
                                    color = MTextMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                if (filteredSources.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hakuna vyanzo vilivyopatikana.",
                                color = MTextMuted,
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
                    color = MTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Aina: ${source.sourceTypeDisplay} | Kijiji: ${source.villageName}",
                    color = MTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Show last tested ph parameter preview
                if (source.phLevel != null) {
                    Text(
                        text = "pH: ${source.phLevel} | Bacteria: ${source.bacteriaCount ?: 0} CFU",
                        color = MBlueLight,
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
