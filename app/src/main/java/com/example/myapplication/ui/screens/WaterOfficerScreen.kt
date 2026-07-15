package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.myapplication.data.DamageReport
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun WaterOfficerScreen(
    onNavigateBack: () -> Unit
) {
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Assigned/Active, 1: Resolved/Closed
    val tabs = listOf("MAJUKUMU YANGU (ACTIVE)", "YALIYOTATULIWA (RESOLVED)")

    val scope = rememberCoroutineScope()

    val loadOfficerData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val repRes = ApiClient.getDamageReports()
                if (repRes.isSuccess) {
                    // Filter in frontend to ensure only assigned to this officer,
                    // although backend already filters, doing it is safer.
                    val currentUser = ApiClient.currentUser
                    reports = repRes.getOrThrow().filter {
                        it.assignedToId == currentUser?.id
                    }
                } else {
                    errorMessage = repRes.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOfficerData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MButton(
                text = "← DASHBOARD",
                onClick = onNavigateBack
            )
            Text(
                text = "KAZI ZA AFISA WA MAJI",
                color = MTextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MDarkGray,
            contentColor = MTextWhite,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, MBorderGray, RectangleShape),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MBlueLight
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MTextWhite)
            }
        } else {
            val filteredReports = when (selectedTab) {
                0 -> reports.filter { it.status == "assigned" || it.status == "in_progress" }
                else -> reports.filter { it.status == "resolved" || it.status == "closed" }
            }

            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hakuna majukumu hapa.",
                        color = MTextMuted,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredReports) { report ->
                        OfficerReportCard(
                            report = report,
                            onActionSuccess = { loadOfficerData() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfficerReportCard(
    report: DamageReport,
    onActionSuccess: () -> Unit
) {
    var isExpanding by remember { mutableStateOf(false) }
    var resolutionNotes by remember { mutableStateOf("") }
    var showResolveInput by remember { mutableStateOf(false) }
    var isOperating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    MCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title.uppercase(),
                    color = MTextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Chanzo: ${report.waterSourceName}",
                    color = MTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Tarehe: ${report.reportDate}",
                    color = MTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                PriorityBadge(priority = report.priority)
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = report.status)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Mtoa Taarifa: ${report.reportedByName}",
            color = MBlueLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = report.description,
            color = MTextMuted,
            fontSize = 13.sp,
            maxLines = if (isExpanding) Int.MAX_VALUE else 2
        )

        if (report.description.length > 80) {
            Text(
                text = if (isExpanding) "SOMA KIPUNGUZO (LESS)" else "SOMA ZAIDI (MORE)",
                color = MTextWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable { isExpanding = !isExpanding }
                    .padding(vertical = 4.dp)
            )
        }

        if (report.status == "resolved" && report.resolutionNotes != null && report.resolutionNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Maelezo ya Utatuzi: ${report.resolutionNotes}",
                color = Color(0xFF4CAF50),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Action controls
        if (report.status == "assigned" || report.status == "in_progress") {
            Spacer(modifier = Modifier.height(12.dp))
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

            if (isOperating) {
                CircularProgressIndicator(color = MTextWhite, modifier = Modifier.size(20.dp))
            } else if (showResolveInput) {
                MTextField(
                    value = resolutionNotes,
                    onValueChange = { resolutionNotes = it },
                    label = "Maelezo ya Utatuzi (Notes)",
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MButton(
                        text = "THIBITISHA UTATUZI",
                        onClick = {
                            if (resolutionNotes.isBlank()) return@MButton
                            isOperating = true
                            scope.launch {
                                val res = ApiClient.resolveDamageReport(report.id, resolutionNotes)
                                isOperating = false
                                if (res.isSuccess) {
                                    onActionSuccess()
                                }
                            }
                        },
                        borderColor = Color(0xFF4CAF50),
                        contentColor = Color(0xFF4CAF50)
                    )
                    MButton(
                        text = "GHAIRI",
                        onClick = { showResolveInput = false }
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (report.status == "assigned") {
                        MButton(
                            text = "ANZA KAZI (START WORK)",
                            onClick = {
                                isOperating = true
                                scope.launch {
                                    val res = ApiClient.startWorkDamageReport(report.id)
                                    isOperating = false
                                    if (res.isSuccess) {
                                        onActionSuccess()
                                    }
                                }
                            },
                            borderColor = Color(0xFFFFEB3B),
                            contentColor = Color(0xFFFFEB3B)
                        )
                        MButton(
                            text = "TUMA WILAYANI (FORWARD)",
                            onClick = {
                                isOperating = true
                                scope.launch {
                                    val res = ApiClient.forwardToDistrict(report.id)
                                    isOperating = false
                                    if (res.isSuccess) {
                                        onActionSuccess()
                                    }
                                }
                            },
                            borderColor = Color(0xFF9C27B0),
                            contentColor = Color(0xFF9C27B0)
                        )
                    } else if (report.status == "in_progress") {
                        MButton(
                            text = "TATUA RIPOTI (RESOLVE)",
                            onClick = { showResolveInput = true },
                            borderColor = Color(0xFF4CAF50),
                            contentColor = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

// Separate Screen for Logging quality report
@Composable
fun LogQualityScreen(
    waterSourceId: Int,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var phText by remember { mutableStateOf("7.0") }
    var bacteriaText by remember { mutableStateOf("0") }
    var ironText by remember { mutableStateOf("0.1") }
    var turbidityText by remember { mutableStateOf("1.5") }
    var chlorineText by remember { mutableStateOf("0.2") }
    var notes by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MButton(
                text = "← CHANZO DETAILS",
                onClick = onNavigateBack
            )
            Text(
                text = "PIMA MAJI YA CHANZO",
                color = MTextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                MTextField(
                    value = phText,
                    onValueChange = { phText = it },
                    label = "pH Level (Range: 0 - 14)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = bacteriaText,
                    onValueChange = { bacteriaText = it },
                    label = "Bacteria Count (CFU/100ml)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = ironText,
                    onValueChange = { ironText = it },
                    label = "Iron (Chuma) Level (mg/L)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = turbidityText,
                    onValueChange = { turbidityText = it },
                    label = "Turbidity (NTU)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = chlorineText,
                    onValueChange = { chlorineText = it },
                    label = "Chlorine (Klorini) Level (mg/L, optional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Maelezo ya Vipimo (Inspector Notes)",
                    singleLine = false,
                    enabled = !isLoading,
                    modifier = Modifier.height(100.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MTextWhite)
                    }
                } else {
                    MButton(
                        text = "TUMA VIPIMO (SUBMIT INSPECTION)",
                        onClick = {
                            val ph = phText.toDoubleOrNull()
                            val bacteria = bacteriaText.toIntOrNull()
                            val iron = ironText.toDoubleOrNull()
                            val turbidity = turbidityText.toDoubleOrNull()
                            val chlorine = chlorineText.toDoubleOrNull()

                            if (ph == null || bacteria == null || iron == null || turbidity == null) {
                                errorMessage = "Tafadhali jaza vipimo vyote vya lazima katika namba sahihi."
                                return@MButton
                            }

                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                val res = ApiClient.submitQualityReport(
                                    waterSourceId = waterSourceId,
                                    phLevel = ph,
                                    bacteriaCount = bacteria,
                                    ironLevel = iron,
                                    turbidity = turbidity,
                                    chlorineLevel = chlorine,
                                    notes = notes
                                )
                                isLoading = false
                                if (res.isSuccess) {
                                    onSuccess()
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message ?: "Imeshindwa kutuma vipimo."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = Color(0xFF4CAF50),
                        contentColor = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}
