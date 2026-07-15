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

@Composable
fun DistrictOfficerScreen(
    onNavigateBack: () -> Unit
) {
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
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
                    reports = repRes.getOrThrow().filter { it.status == "forwarded_to_district" }
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
        loadDistrictData()
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
            val user = ApiClient.currentUser
            Column {
                Text(
                    text = "DASHBOARD YA WILAYA",
                    color = MTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Afisa: ${user?.displayName?.uppercase() ?: ""}",
                    color = MBlueLight,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            MButton(
                text = "REFRESH",
                onClick = { loadDistrictData() }
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MTextWhite)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = errorMessage!!,
                    color = MRed,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else if (reports.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hakuna ripoti zilizotumwa wilayani.",
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
                items(reports) { report ->
                    DistrictReportCard(report = report)
                }
            }
        }
    }
}

@Composable
fun DistrictReportCard(report: DamageReport) {
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
            text = report.description,
            color = MTextWhite,
            fontSize = 13.sp
        )
        
        // As a district officer, they might review it and allocate budget, etc.
        // For now, this is a read-only view of escalated tasks.
        Spacer(modifier = Modifier.height(12.dp))
        MStripesDivider(height = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Imeletwa na Kijiji: ${report.forwardedAt ?: "Haijulikani"}",
            color = MBlueLight,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
