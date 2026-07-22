package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.User
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictOfficerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddWaterSource: () -> Unit,
    onNavigateToAddVillage: () -> Unit
) {
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var workers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Forwarded, 1: Active, 2: Resolved, 3: All

    val tabs = listOf("ZILIZOTUMWA", "INAFANYIWA KAZI", "IMETATULIWA", "ZOTE")
    val scope = rememberCoroutineScope()

    val loadDistrictData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val repRes = ApiClient.getDamageReports()
                if (repRes.isSuccess) {
                    // District officer sees all forwarded, assigned, in_progress, and resolved/closed reports
                    reports = repRes.getOrThrow().filter {
                        it.status == "forwarded_to_district" || it.status == "assigned" ||
                                it.status == "in_progress" || it.status == "resolved" || it.status == "closed"
                    }
                } else {
                    errorMessage = repRes.exceptionOrNull()?.message
                }
                
                val workRes = ApiClient.getWaterOfficers()
                if (workRes.isSuccess) {
                    workers = workRes.getOrThrow()
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadDistrictData()
    }

    val filteredReports = when (selectedTab) {
        0 -> reports.filter { it.status == "forwarded_to_district" }
        1 -> reports.filter { it.status == "assigned" || it.status == "in_progress" }
        2 -> reports.filter { it.status == "resolved" || it.status == "closed" }
        else -> reports
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            val user = ApiClient.currentUser
            Column {
                Text(
                    text = "DASHBOARD YA WILAYA",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Afisa: ${user?.displayName?.uppercase() ?: ""}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                MButton(
                    text = "REFRESH",
                    onClick = { loadDistrictData() },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MButton(
                        text = "+ CHANZO",
                        onClick = onNavigateToAddWaterSource,
                        backgroundColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        borderColor = Color(0xFF4CAF50)
                    )
                    MButton(
                        text = "+ KIJIJI",
                        onClick = onNavigateToAddVillage,
                        backgroundColor = Color(0xFFFF9800),
                        contentColor = Color.White,
                        borderColor = Color(0xFFFF9800)
                    )
                }
            }
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 12.dp))

        // Tabs
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else if (filteredReports.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hakuna ripoti katika kundi hili.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    DistrictReportCard(
                        report = report,
                        workers = workers,
                        onActionSuccess = { loadDistrictData() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictReportCard(
    report: DamageReport,
    workers: List<User>,
    onActionSuccess: () -> Unit
) {
    var selectedWorker by remember { mutableStateOf<User?>(null) }
    var workerDropdownExpanded by remember { mutableStateOf(false) }
    var isOperating by remember { mutableStateOf(false) }

    var showResolveInput by remember { mutableStateOf(false) }
    var resolutionNotes by remember { mutableStateOf("") }

    var showRejectInput by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(workers) {
        if (workers.isNotEmpty() && selectedWorker == null) {
            selectedWorker = workers.first()
        }
    }

    CleanReportCard(
        report = report,
        actions = {
            Spacer(modifier = Modifier.height(8.dp))
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            
            if (report.forwardedAt != null) {
                Text(
                    text = "Imeletwa Wilayani: ${report.forwardedAt}",
                    color = SubtleOnWhite,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            
            if (report.assignedToName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Afisa Maji aliyepangiwa: ${report.assignedToName}",
                    color = Color(0xFF00BCD4),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (report.resolutionNotes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Maelezo ya Utatuzi: ${report.resolutionNotes}",
                    color = Color(0xFF4CAF50),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (isOperating) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(color = BlueNight, modifier = Modifier.size(20.dp))
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                // Resolve input form
                if (showResolveInput) {
                    MTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = "Maelezo ya Utatuzi",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MButton(
                            text = "THIBITISHA TATIJO",
                            onClick = {
                                if (resolutionNotes.isBlank()) return@MButton
                                isOperating = true
                                scope.launch {
                                    val res = ApiClient.resolveDamageReport(report.id, resolutionNotes)
                                    isOperating = false
                                    if (res.isSuccess) onActionSuccess()
                                }
                            },
                            backgroundColor = Color(0xFF4CAF50),
                            contentColor = WhitePure,
                            borderColor = Color(0xFF4CAF50)
                        )
                        MButton(text = "GHAIRI", onClick = { showResolveInput = false })
                    }
                }

                // Reject input form
                if (showRejectInput) {
                    MTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = "Sababu ya Kukataa",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MButton(
                            text = "THIBITISHA KATAA",
                            onClick = {
                                if (rejectionReason.isBlank()) return@MButton
                                isOperating = true
                                scope.launch {
                                    val res = ApiClient.rejectDamageReport(report.id, rejectionReason)
                                    isOperating = false
                                    if (res.isSuccess) onActionSuccess()
                                }
                            },
                            backgroundColor = Color.Red,
                            contentColor = WhitePure,
                            borderColor = Color.Red
                        )
                        MButton(text = "GHAIRI", onClick = { showRejectInput = false })
                    }
                }

                if (!showResolveInput && !showRejectInput) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Assign Water Officer Dropdown
                        if (workers.isNotEmpty() && report.status != "resolved" && report.status != "closed") {
                            Text(
                                text = "PANGA AFISA WA MAJI (ASSIGN WORKER):",
                                color = BlueNight,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, BlueFoam, RoundedCornerShape(16.dp))
                                        .background(WhitePure)
                                        .clickable { workerDropdownExpanded = true }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = selectedWorker?.username ?: "Chagua Mfanyakazi",
                                            color = BlueNight,
                                            fontSize = 13.sp
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            tint = BlueNight
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = workerDropdownExpanded,
                                        onDismissRequest = { workerDropdownExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .background(WhitePure)
                                            .border(1.dp, BlueFoam, RoundedCornerShape(16.dp))
                                    ) {
                                        workers.forEach { w ->
                                            DropdownMenuItem(
                                                text = { Text(w.username, color = BlueNight) },
                                                onClick = {
                                                    selectedWorker = w
                                                    workerDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                MButton(
                                    text = "ASSIGN",
                                    onClick = {
                                        val wk = selectedWorker ?: return@MButton
                                        isOperating = true
                                        scope.launch {
                                            val res = ApiClient.assignDamageReport(report.id, wk.id)
                                            isOperating = false
                                            if (res.isSuccess) onActionSuccess()
                                        }
                                    },
                                    borderColor = BlueOcean,
                                    contentColor = WhitePure,
                                    backgroundColor = BlueOcean
                                )
                            }
                        }

                        // District action buttons
                        if (report.status != "resolved" && report.status != "closed") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MButton(
                                    text = "TATUA",
                                    onClick = { showResolveInput = true },
                                    backgroundColor = Color(0xFF4CAF50),
                                    contentColor = WhitePure,
                                    borderColor = Color(0xFF4CAF50)
                                )
                                MButton(
                                    text = "KATAA",
                                    onClick = { showRejectInput = true },
                                    backgroundColor = Color.Red,
                                    contentColor = WhitePure,
                                    borderColor = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
