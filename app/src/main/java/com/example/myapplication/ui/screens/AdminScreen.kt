package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun AdminScreen(
    onNavigateToCitizen: () -> Unit,
    onNavigateToLeader: () -> Unit,
    onNavigateToOfficer: () -> Unit,
    onNavigateToDistrict: () -> Unit,
    onNavigateToAddWaterSource: () -> Unit,
    onNavigateToAddVillage: () -> Unit
) {
    val user = ApiClient.currentUser
    val scope = rememberCoroutineScope()

    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var workers by remember { mutableStateOf<List<User>>(emptyList()) }
    var totalSources by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // SweetAlert dialog state
    var sweetAlertData by remember { mutableStateOf<SweetAlertData?>(null) }

    // selectedTab: 0 -> Zote, 1 -> Zinazosubiri, 2 -> Zimetatuliwa, 3 -> Zimekataliwa
    var selectedTab by remember { mutableStateOf(0) }

    val loadAdminData = {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val rpt = ApiClient.getDamageReports()
                if (rpt.isSuccess) {
                    reports = rpt.getOrThrow()
                } else {
                    errorMessage = rpt.exceptionOrNull()?.message
                }

                val wrk = ApiClient.getWaterOfficers()
                if (wrk.isSuccess) {
                    workers = wrk.getOrThrow()
                }

                val src = ApiClient.getWaterSources()
                if (src.isSuccess) {
                    totalSources = src.getOrThrow().size
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAdminData()
    }

    val totalReports = reports.size
    val pendingReports = reports.count {
        it.status == "pending_village" || it.status == "pending" || it.status == "forwarded_to_district"
    }
    val resolvedReports = reports.count { it.status == "resolved" || it.status == "closed" }
    val rejectedReports = reports.count { it.status == "rejected" }

    val filteredReports = when (selectedTab) {
        0 -> reports
        1 -> reports.filter {
            it.status == "pending_village" || it.status == "pending" || it.status == "forwarded_to_district"
        }
        2 -> reports.filter { it.status == "resolved" || it.status == "closed" }
        3 -> reports.filter { it.status == "rejected" }
        else -> reports
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        iconTint = BlueOcean,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        value = pendingReports.toString(),
                        label = "Zinasubiri\nIdhini",
                        icon = Icons.Default.HourglassTop,
                        iconTint = BlueDeep,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        value = resolvedReports.toString(),
                        label = "Zimetatuliwa",
                        icon = Icons.Default.CheckCircle,
                        iconTint = BlueAbyss,
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        value = totalSources.toString(),
                        label = "Vyanzo\nvya Maji",
                        icon = Icons.Default.WaterDrop,
                        iconTint = BlueOcean,
                        isSelected = false,
                        onClick = onNavigateToCitizen
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
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        AdminNavTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Add,
                            title = "Ongeza Chanzo",
                            subtitle = "Sajili Chanzo Kipya",
                            accent = Color(0xFF4CAF50),
                            onClick = onNavigateToAddWaterSource
                        )
                        AdminNavTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Add,
                            title = "Ongeza Kijiji",
                            subtitle = "Sajili Kijiji Kipya",
                            accent = Color(0xFFFF9800),
                            onClick = onNavigateToAddVillage
                        )
                    }
                }
            }

            // ── Report Management Section Header ────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "USIMAMIZI WA RIPOTI",
                            color = BlueNight,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Angalia na uteleze majukumu ya admin kwa ripoti",
                            color = SubtleOnWhite,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    MButton(
                        text = "REFRESH",
                        onClick = {
                            loadAdminData()
                            sweetAlertData = SweetAlertData(
                                title = "Taarifa Zimehuishwa",
                                message = "Ripoti na takwimu zimesasishwa kutoka kwenye mfumo.",
                                type = SweetAlertType.INFO
                            )
                        }
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            // ── Report Filter Tabs ────────────────────────────────────────────────
            item {
                val tabs = listOf(
                    "ZOTE ($totalReports)",
                    "ZINASUBIRI ($pendingReports)",
                    "ZIMETATULIWA ($resolvedReports)",
                    "ZIMEKATALIWA ($rejectedReports)"
                )
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = WhitePure,
                    contentColor = BlueNight,
                    edgePadding = 20.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .border(1.dp, BlueFoam, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Report List Items ────────────────────────────────────────────────
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BlueOcean)
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else if (filteredReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hakuna ripoti katika kundi hili.",
                            color = SubtleOnWhite,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                items(filteredReports) { report ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        AdminReportCard(
                            report = report,
                            workers = workers,
                            onActionSuccess = { loadAdminData() },
                            onShowSweetAlert = { alert -> sweetAlertData = alert }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        // Render SweetAlert Dialog if active
        sweetAlertData?.let { data ->
            SweetAlertDialog(
                data = data,
                onDismissRequest = { sweetAlertData = null }
            )
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
    iconTint: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlueOcean.copy(alpha = 0.15f) else WhitePure
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, BlueOcean) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 3.dp)
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
                color = if (isSelected) BlueOcean else SubtleOnWhite,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
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

// ── Admin Report Card ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportCard(
    report: DamageReport,
    workers: List<User>,
    onActionSuccess: () -> Unit,
    onShowSweetAlert: (SweetAlertData) -> Unit
) {
    var selectedWorker by remember { mutableStateOf<User?>(null) }
    var workerDropdownExpanded by remember { mutableStateOf(false) }
    var isOperating by remember { mutableStateOf(false) }

    var showRejectInput by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    var showResolveInput by remember { mutableStateOf(false) }
    var resolutionNotes by remember { mutableStateOf("") }

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
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(vertical = 6.dp))

            // Extra metadata
            Column(modifier = Modifier.fillMaxWidth()) {
                val reporterDisplayName = if (report.reportedByName.isBlank() || report.reportedByName.equals("Anonymous", ignoreCase = true)) {
                    "Mwananchi"
                } else {
                    report.reportedByName
                }
                Text(
                    text = "Imeripotiwa na: $reporterDisplayName",
                    color = BlueNight,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (report.assignedToName != null) {
                    Text(
                        text = "Afisa Maji: ${report.assignedToName}",
                        color = Color(0xFF00BCD4),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (report.rejectionReason?.isNotEmpty() == true) {
                    Text(
                        text = "Sababu ya Kukataliwa: ${report.rejectionReason}",
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (report.resolutionNotes?.isNotEmpty() == true) {
                    Text(
                        text = "Maelezo ya Utatuzi: ${report.resolutionNotes}",
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isOperating) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BlueOcean, modifier = Modifier.size(24.dp))
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    
                    // 1. Assign Water Officer section
                    if (workers.isNotEmpty() && report.status != "resolved" && report.status != "closed") {
                        Text(
                            text = "PANGA AFISA WA MAJI (ASSIGN WORKER):",
                            color = BlueNight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, BlueFoam, RoundedCornerShape(12.dp))
                                    .background(WhitePure)
                                    .clickable { workerDropdownExpanded = true }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedWorker?.username ?: "Chagua Mfanyakazi",
                                        color = BlueNight,
                                        fontSize = 12.sp
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
                                        .fillMaxWidth(0.7f)
                                        .background(WhitePure)
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
                                        if (res.isSuccess) {
                                            onActionSuccess()
                                            onShowSweetAlert(
                                                SweetAlertData(
                                                    title = "Umefanikiwa!",
                                                    message = "Mfanyakazi ${wk.username} amepangiwa majukumu kikamilifu.",
                                                    type = SweetAlertType.SUCCESS
                                                )
                                            )
                                        } else {
                                            onShowSweetAlert(
                                                SweetAlertData(
                                                    title = "Imeshindwa",
                                                    message = res.exceptionOrNull()?.message ?: "Imeshindwa kupanga mfanyakazi.",
                                                    type = SweetAlertType.ERROR
                                                )
                                            )
                                        }
                                    }
                                },
                                backgroundColor = BlueOcean,
                                contentColor = WhitePure,
                                borderColor = BlueOcean
                            )
                        }
                    }

                    // 2. Reject input form if active
                    if (showRejectInput) {
                        MTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            label = "Sababu ya Kukataa Ripoti",
                            modifier = Modifier.fillMaxWidth()
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
                                                    title = "Ripoti Imekataliwa",
                                                    message = "Ripoti imekataliwa na kuwekewa sababu.",
                                                    type = SweetAlertType.WARNING
                                                )
                                            )
                                        } else {
                                            onShowSweetAlert(
                                                SweetAlertData(
                                                    title = "Imeshindwa",
                                                    message = res.exceptionOrNull()?.message ?: "Imeshindwa kukataa ripoti.",
                                                    type = SweetAlertType.ERROR
                                                )
                                            )
                                        }
                                    }
                                },
                                backgroundColor = Color.Red,
                                contentColor = WhitePure,
                                borderColor = Color.Red
                            )
                            MButton(
                                text = "GHAIRI",
                                onClick = { showRejectInput = false }
                            )
                        }
                    }

                    // 3. Resolve input form if active
                    if (showResolveInput) {
                        MTextField(
                            value = resolutionNotes,
                            onValueChange = { resolutionNotes = it },
                            label = "Maelezo ya Utatuzi wa Matatizo",
                            modifier = Modifier.fillMaxWidth()
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
                                                    message = "Ripoti imetatuliwa na kuhifadhiwa kikamilifu.",
                                                    type = SweetAlertType.SUCCESS
                                                )
                                            )
                                        } else {
                                            onShowSweetAlert(
                                                SweetAlertData(
                                                    title = "Imeshindwa",
                                                    message = res.exceptionOrNull()?.message ?: "Imeshindwa kutatua ripoti.",
                                                    type = SweetAlertType.ERROR
                                                )
                                            )
                                        }
                                    }
                                },
                                backgroundColor = Color(0xFF4CAF50),
                                contentColor = WhitePure,
                                borderColor = Color(0xFF4CAF50)
                            )
                            MButton(
                                text = "GHAIRI",
                                onClick = { showResolveInput = false }
                            )
                        }
                    }

                    // Main Action Buttons Row for Admin (Responsive FlowRow)
                    if (!showRejectInput && !showResolveInput) {
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Approve Button (if pending)
                            if (report.status == "pending_village" || report.status == "pending") {
                                MButton(
                                    text = "IDHINISHA",
                                    onClick = {
                                        isOperating = true
                                        scope.launch {
                                            val res = ApiClient.approveDamageReport(report.id)
                                            isOperating = false
                                            if (res.isSuccess) {
                                                onActionSuccess()
                                                onShowSweetAlert(
                                                    SweetAlertData(
                                                        title = "Imefanikiwa!",
                                                        message = "Ripoti imeidhinishwa kikamilifu.",
                                                        type = SweetAlertType.SUCCESS
                                                    )
                                                )
                                            } else {
                                                onShowSweetAlert(
                                                    SweetAlertData(
                                                        title = "Imeshindwa",
                                                        message = res.exceptionOrNull()?.message ?: "Imeshindwa kuidhinisha ripoti.",
                                                        type = SweetAlertType.ERROR
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    backgroundColor = Color(0xFF4CAF50),
                                    contentColor = WhitePure,
                                    borderColor = Color(0xFF4CAF50)
                                )
                            }

                            // Forward to District Button (if pending or approved by village)
                            if (report.status == "village_approved" || report.status == "pending") {
                                MButton(
                                    text = "WILAYANI",
                                    onClick = {
                                        isOperating = true
                                        scope.launch {
                                            val res = ApiClient.forwardToDistrict(report.id)
                                            isOperating = false
                                            if (res.isSuccess) {
                                                onActionSuccess()
                                                onShowSweetAlert(
                                                    SweetAlertData(
                                                        title = "Imetumwa!",
                                                        message = "Ripoti imetumwa Wilayani kikamilifu.",
                                                        type = SweetAlertType.SUCCESS
                                                    )
                                                )
                                            } else {
                                                onShowSweetAlert(
                                                    SweetAlertData(
                                                        title = "Imeshindwa",
                                                        message = res.exceptionOrNull()?.message ?: "Imeshindwa kutuma wilayani.",
                                                        type = SweetAlertType.ERROR
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    backgroundColor = BlueOcean,
                                    contentColor = WhitePure,
                                    borderColor = BlueOcean
                                )
                            }

                            // Resolve Button (if not resolved)
                            if (report.status != "resolved" && report.status != "closed") {
                                MButton(
                                    text = "TATUA",
                                    onClick = { showResolveInput = true },
                                    backgroundColor = Color(0xFF009688),
                                    contentColor = WhitePure,
                                    borderColor = Color(0xFF009688)
                                )
                            }

                            // Reject Button (if not rejected)
                            if (report.status != "rejected" && report.status != "resolved") {
                                MButton(
                                    text = "KATAA",
                                    onClick = { showRejectInput = true },
                                    backgroundColor = Color(0xFFFF9800),
                                    contentColor = WhitePure,
                                    borderColor = Color(0xFFFF9800)
                                )
                            }

                            // Delete Button - ONLY DELETE maintains confirmation dialog prompt!
                            MButton(
                                text = "FUTA",
                                onClick = {
                                    onShowSweetAlert(
                                        SweetAlertData(
                                            title = "Thibitisha Kufuta",
                                            message = "Je, una uhakika unataka kufuta ripoti hii kabisa kutoka kwenye mfumo?",
                                            type = SweetAlertType.ERROR,
                                            confirmButtonText = "Ndio, Futa Kabisa",
                                            cancelButtonText = "Hapana",
                                            onConfirm = {
                                                isOperating = true
                                                scope.launch {
                                                    val res = ApiClient.deleteDamageReport(report.id)
                                                    isOperating = false
                                                    if (res.isSuccess) {
                                                        onActionSuccess()
                                                        onShowSweetAlert(
                                                            SweetAlertData(
                                                                title = "Imefutwa!",
                                                                message = "Ripoti imefutwa kabisa kutoka kwenye mfumo.",
                                                                type = SweetAlertType.ERROR
                                                            )
                                                        )
                                                    } else {
                                                        onShowSweetAlert(
                                                            SweetAlertData(
                                                                title = "Imeshindwa Kufuta",
                                                                message = res.exceptionOrNull()?.message ?: "Imeshindwa kufuta ripoti.",
                                                                type = SweetAlertType.ERROR
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    )
                                },
                                backgroundColor = Color.Red,
                                contentColor = WhitePure,
                                borderColor = Color.Red
                            )
                        }
                    }
                }
            }
        }
    )
}
