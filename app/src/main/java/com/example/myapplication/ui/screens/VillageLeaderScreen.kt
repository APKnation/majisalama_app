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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.User
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch

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
    
    val scope = rememberCoroutineScope()

    // Fetch data
    val loadLeaderData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                // Fetch reports
                val repRes = ApiClient.getDamageReports()
                if (repRes.isSuccess) {
                    reports = repRes.getOrThrow()
                } else {
                    errorMessage = repRes.exceptionOrNull()?.message
                }

                // Fetch water officers for assignment
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
                text = "IDHINI ZA MWENYEKITI",
                color = MTextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MDarkGray,
            contentColor = MTextWhite,
            edgePadding = 0.dp,
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
                        LeaderReportCard(
                            report = report,
                            workers = workers,
                            onActionSuccess = { loadLeaderData() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderReportCard(
    report: DamageReport,
    workers: List<User>,
    onActionSuccess: () -> Unit
) {
    var isExpanding by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }
    var showRejectInput by remember { mutableStateOf(false) }
    var selectedWorker by remember { mutableStateOf<User?>(null) }
    var workerDropdownExpanded by remember { mutableStateOf(false) }
    var isOperating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Setup initial selected worker
    LaunchedEffect(workers) {
        if (workers.isNotEmpty() && selectedWorker == null) {
            selectedWorker = workers.first()
        }
    }

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

        if (report.status == "rejected" && report.rejectionReason.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sababu ya Kukataliwa: ${report.rejectionReason}",
                color = Color.Red,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

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

        // Action controls for pending village leader decisions
        if (report.status == "pending_village" || report.status == "pending") {
            Spacer(modifier = Modifier.height(12.dp))
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

            if (isOperating) {
                CircularProgressIndicator(color = MTextWhite, modifier = Modifier.size(20.dp))
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
                                }
                            }
                        },
                        borderColor = MRed,
                        contentColor = MRed
                    )
                    MButton(
                        text = "GHAIRI",
                        onClick = { showRejectInput = false }
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MButton(
                        text = "IDHINISHA (APPROVE)",
                        onClick = {
                            isOperating = true
                            scope.launch {
                                val res = ApiClient.approveDamageReport(report.id)
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
                        text = "KATAA (REJECT)",
                        onClick = { showRejectInput = true },
                        borderColor = MRed,
                        contentColor = MRed
                    )
                }
            }
        }

        // Action controls for assigning a worker to approved reports
        if ((report.status == "village_approved" || report.status == "forwarded_to_district" || report.status == "assigned") && workers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

            if (isOperating) {
                CircularProgressIndicator(color = MTextWhite, modifier = Modifier.size(20.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PANGA MFANYAKAZI (ASSIGN WATER OFFICER)",
                        color = MTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Dropdown
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MBorderGray, RectangleShape)
                                .background(MBlack)
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
                                    color = MTextWhite,
                                    fontSize = 13.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MTextWhite
                                )
                            }
                            DropdownMenu(
                                expanded = workerDropdownExpanded,
                                onDismissRequest = { workerDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .background(MDarkGray)
                                    .border(1.dp, MBorderGray, RectangleShape)
                            ) {
                                workers.forEach { w ->
                                    DropdownMenuItem(
                                        text = { Text(w.username, color = MTextWhite) },
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
                            borderColor = Color(0xFF00BCD4),
                            contentColor = Color(0xFF00BCD4)
                        )
                    }
                }
            }
        }
    }
}
