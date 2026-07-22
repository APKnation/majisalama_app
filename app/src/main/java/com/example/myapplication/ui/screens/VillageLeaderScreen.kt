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
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.User
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillageLeaderScreen(
    onNavigateBack: () -> Unit
) {
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var workers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Approved/Active, 2: Resolved/Closed, 3: Rejected
    val tabs = listOf("INASUBIRI (PENDING)", "IMESIMAMIWA (ACTIVE)", "IMETATULIWA (RESOLVED)", "IMEKATALIWA (REJECTED)")
    
    // SweetAlert dialog state
    var sweetAlertData by remember { mutableStateOf<SweetAlertData?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch data
    val loadLeaderData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val repRes = ApiClient.getDamageReports()
                if (repRes.isSuccess) {
                    reports = repRes.getOrThrow()
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
        loadLeaderData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                MButton(
                    text = "← DASHBOARD",
                    onClick = onNavigateBack
                )
                Text(
                    text = "IDHINI ZA MWENYEKITI",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                MButton(
                    text = "REFRESH",
                    onClick = {
                        loadLeaderData()
                        sweetAlertData = SweetAlertData(
                            title = "Taarifa Zimehuishwa",
                            message = "Taarifa za idhini za mwenyekiti zimesasishwa.",
                            type = SweetAlertType.INFO
                        )
                    }
                )
            }

            MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

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
            } else {
                // Filter reports by tab
                val filteredReports = when (selectedTab) {
                    0 -> reports.filter { it.status == "pending_village" || it.status == "pending" }
                    1 -> reports.filter {
                        it.status == "village_approved" || it.status == "forwarded_to_district" ||
                                it.status == "assigned" || it.status == "in_progress"
                    }
                    2 -> reports.filter { it.status == "resolved" || it.status == "closed" }
                    else -> reports.filter { it.status == "rejected" }
                }

                if (filteredReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
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
                            LeaderReportCard(
                                report = report,
                                workers = workers,
                                onActionSuccess = { loadLeaderData() },
                                onShowSweetAlert = { alert -> sweetAlertData = alert }
                            )
                        }
                    }
                }
            }
        }

        sweetAlertData?.let { data ->
            SweetAlertDialog(
                data = data,
                onDismissRequest = { sweetAlertData = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderReportCard(
    report: DamageReport,
    workers: List<User>,
    onActionSuccess: () -> Unit,
    onShowSweetAlert: (SweetAlertData) -> Unit
) {
    var rejectionReason by remember { mutableStateOf("") }
    var showRejectInput by remember { mutableStateOf(false) }

    var resolutionNotes by remember { mutableStateOf("") }
    var showResolveInput by remember { mutableStateOf(false) }

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
            if (report.status == "rejected" && report.rejectionReason?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sababu ya Kukataliwa: ${report.rejectionReason}",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (report.status == "resolved" && report.resolutionNotes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Maelezo ya Utatuzi: ${report.resolutionNotes}",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (report.assignedToName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Afisa aliyepangiwa: ${report.assignedToName}",
                    color = Color(0xFF00BCD4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Action controls for pending village leader decisions
            if (report.status == "pending_village" || report.status == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                MStripesDivider(height = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

                if (isOperating) {
                    CircularProgressIndicator(color = com.example.myapplication.ui.theme.BlueNight, modifier = Modifier.size(20.dp))
                } else if (showRejectInput) {
                    MTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = "Sababu ya Kukataa",
                        modifier = Modifier.padding(bottom = 12.dp)
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
                                    if (res.isSuccess) {
                                        onActionSuccess()
                                        showRejectInput = false
                                        onShowSweetAlert(
                                            SweetAlertData(
                                                title = "Imekataliwa",
                                                message = "Ripoti imekataliwa na kuwekewa sababu.",
                                                type = SweetAlertType.WARNING
                                            )
                                        )
                                    }
                                }
                            },
                            borderColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                        MButton(
                            text = "GHAIRI",
                            onClick = { showRejectInput = false }
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MButton(
                            text = "IDHINISHA",
                            onClick = {
                                onShowSweetAlert(
                                    SweetAlertData(
                                        title = "Thibitisha Idhini",
                                        message = "Je, una uhakika unataka kuidhinisha ripoti hii kama Mwenyekiti?",
                                        type = SweetAlertType.CONFIRM,
                                        confirmButtonText = "Ndio, Idhinisha",
                                        cancelButtonText = "Ghairi",
                                        onConfirm = {
                                            isOperating = true
                                            scope.launch {
                                                val res = ApiClient.approveDamageReport(report.id)
                                                isOperating = false
                                                if (res.isSuccess) {
                                                    onActionSuccess()
                                                    onShowSweetAlert(
                                                        SweetAlertData(
                                                            title = "Imefanikiwa!",
                                                            message = "Ripoti imeidhinishwa kikamilifu na Mwenyekiti.",
                                                            type = SweetAlertType.SUCCESS
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    )
                                )
                            },
                            borderColor = Color(0xFF4CAF50),
                            contentColor = WhitePure,
                            backgroundColor = Color(0xFF4CAF50)
                        )
                        MButton(
                            text = "PELEKA WILAYANI",
                            onClick = {
                                onShowSweetAlert(
                                    SweetAlertData(
                                        title = "Peleka Wilayani",
                                        message = "Je, una uhakika unataka kupeleka ripoti hii kwa Afisa wa Wilaya?",
                                        type = SweetAlertType.CONFIRM,
                                        confirmButtonText = "Ndio, Peleka",
                                        cancelButtonText = "Ghairi",
                                        onConfirm = {
                                            isOperating = true
                                            scope.launch {
                                                val res = ApiClient.forwardToDistrict(report.id)
                                                isOperating = false
                                                if (res.isSuccess) {
                                                    onActionSuccess()
                                                    onShowSweetAlert(
                                                        SweetAlertData(
                                                            title = "Imetumwa Wilayani",
                                                            message = "Ripoti imewasilishwa Wilayani kwa hatua zaidi.",
                                                            type = SweetAlertType.SUCCESS
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    )
                                )
                            },
                            borderColor = BlueOcean,
                            contentColor = WhitePure,
                            backgroundColor = BlueOcean
                        )
                        MButton(
                            text = "KATAA",
                            onClick = { showRejectInput = true },
                            borderColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Action controls for active/approved reports
            if ((report.status == "village_approved" || report.status == "forwarded_to_district" || report.status == "assigned" || report.status == "in_progress") && report.status != "resolved") {
                Spacer(modifier = Modifier.height(12.dp))
                MStripesDivider(height = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

                if (isOperating) {
                    CircularProgressIndicator(color = com.example.myapplication.ui.theme.BlueNight, modifier = Modifier.size(20.dp))
                } else if (showResolveInput) {
                    MTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = "Maelezo ya Utatuzi",
                        modifier = Modifier.padding(bottom = 12.dp)
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
                                    if (res.isSuccess) {
                                        onActionSuccess()
                                        showResolveInput = false
                                        onShowSweetAlert(
                                            SweetAlertData(
                                                title = "Imetatuliwa!",
                                                message = "Ripoti imetatuliwa na kuwekewa maelezo ya utatuzi.",
                                                type = SweetAlertType.SUCCESS
                                            )
                                        )
                                    }
                                }
                            },
                            borderColor = Color(0xFF4CAF50),
                            contentColor = WhitePure,
                            backgroundColor = Color(0xFF4CAF50)
                        )
                        MButton(
                            text = "GHAIRI",
                            onClick = { showResolveInput = false }
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (workers.isNotEmpty()) {
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
                                        onShowSweetAlert(
                                            SweetAlertData(
                                                title = "Thibitisha Afisa",
                                                message = "Je, una uhakika unataka kumpanga ${wk.username} kushughulikia tatizo hili?",
                                                type = SweetAlertType.CONFIRM,
                                                confirmButtonText = "Ndio, Panga",
                                                cancelButtonText = "Ghairi",
                                                onConfirm = {
                                                    isOperating = true
                                                    scope.launch {
                                                        val res = ApiClient.assignDamageReport(report.id, wk.id)
                                                        isOperating = false
                                                        if (res.isSuccess) {
                                                            onActionSuccess()
                                                            onShowSweetAlert(
                                                                SweetAlertData(
                                                                    title = "Umefanikiwa!",
                                                                    message = "Afisa ${wk.username} amepangiwa majukumu kikamilifu.",
                                                                    type = SweetAlertType.SUCCESS
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            )
                                        )
                                    },
                                    borderColor = com.example.myapplication.ui.theme.BlueOcean,
                                    contentColor = com.example.myapplication.ui.theme.WhitePure,
                                    backgroundColor = com.example.myapplication.ui.theme.BlueOcean
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (report.status != "forwarded_to_district") {
                                MButton(
                                    text = "PELEKA WILAYANI",
                                    onClick = {
                                        onShowSweetAlert(
                                            SweetAlertData(
                                                title = "Peleka Wilayani",
                                                message = "Je, una uhakika unataka kupeleka ripoti hii kwa Afisa wa Wilaya?",
                                                type = SweetAlertType.CONFIRM,
                                                confirmButtonText = "Ndio, Peleka",
                                                cancelButtonText = "Ghairi",
                                                onConfirm = {
                                                    isOperating = true
                                                    scope.launch {
                                                        val res = ApiClient.forwardToDistrict(report.id)
                                                        isOperating = false
                                                        if (res.isSuccess) {
                                                            onActionSuccess()
                                                            onShowSweetAlert(
                                                                SweetAlertData(
                                                                    title = "Imetumwa Wilayani",
                                                                    message = "Ripoti imewasilishwa Wilayani kwa hatua zaidi.",
                                                                    type = SweetAlertType.SUCCESS
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            )
                                        )
                                    },
                                    borderColor = BlueOcean,
                                    contentColor = WhitePure,
                                    backgroundColor = BlueOcean
                                )
                            }
                            MButton(
                                text = "TATUA",
                                onClick = { showResolveInput = true },
                                borderColor = Color(0xFF4CAF50),
                                contentColor = WhitePure,
                                backgroundColor = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    )
}
