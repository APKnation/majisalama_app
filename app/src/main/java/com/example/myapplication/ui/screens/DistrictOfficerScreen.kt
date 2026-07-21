package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

@Composable
fun DistrictOfficerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddWaterSource: () -> Unit
) {
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var workers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    val loadDistrictData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val repRes = ApiClient.getDamageReports()
                if (repRes.isSuccess) {
                    // Filter reports specifically forwarded to the district
                    reports = repRes.getOrThrow().filter { it.status == "forwarded_to_district" || it.status == "assigned" || it.status == "in_progress" || it.status == "resolved" }
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
                MButton(
                    text = "+ ONGEZA CHANZO",
                    onClick = onNavigateToAddWaterSource,
                    backgroundColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    borderColor = Color(0xFF4CAF50)
                )
            }
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

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
        } else if (reports.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hakuna ripoti zilizotumwa wilayani.",
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
                items(reports) { report ->
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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.myapplication.data.User

@Composable
fun DistrictReportCard(
    report: DamageReport,
    workers: List<User>,
    onActionSuccess: () -> Unit
) {
    var selectedWorker by remember { mutableStateOf<User?>(null) }
    var workerDropdownExpanded by remember { mutableStateOf(false) }
    var isOperating by remember { mutableStateOf(false) }
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
            Text(
                text = "Imeletwa na Kijiji: ${report.forwardedAt ?: "Haijulikani"}",
                color = com.example.myapplication.ui.theme.SubtleOnWhite,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            
            if (report.assignedToName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Wafanyakazi: ${report.assignedToName}",
                    color = Color(0xFF00BCD4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (report.status == "forwarded_to_district" && workers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                if (isOperating) {
                    CircularProgressIndicator(color = com.example.myapplication.ui.theme.BlueNight, modifier = Modifier.size(20.dp))
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PANGA MFANYAKAZI (ASSIGN WATER OFFICER)",
                            color = com.example.myapplication.ui.theme.BlueNight,
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
                                    .border(1.dp, com.example.myapplication.ui.theme.BlueFoam, RoundedCornerShape(16.dp))
                                    .background(com.example.myapplication.ui.theme.WhitePure)
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
                                        color = com.example.myapplication.ui.theme.BlueNight,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = com.example.myapplication.ui.theme.BlueNight
                                    )
                                }
                                DropdownMenu(
                                    expanded = workerDropdownExpanded,
                                    onDismissRequest = { workerDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .background(com.example.myapplication.ui.theme.WhitePure)
                                        .border(1.dp, com.example.myapplication.ui.theme.BlueFoam, RoundedCornerShape(16.dp))
                                ) {
                                    workers.forEach { w ->
                                        DropdownMenuItem(
                                            text = { Text(w.username, color = com.example.myapplication.ui.theme.BlueNight) },
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
                                        if (res.isSuccess) {
                                            onActionSuccess()
                                        }
                                    }
                                },
                                borderColor = com.example.myapplication.ui.theme.BlueOcean,
                                contentColor = com.example.myapplication.ui.theme.WhitePure,
                                backgroundColor = com.example.myapplication.ui.theme.BlueOcean
                            )
                        }
                    }
                }
            }
        }
    )
}
