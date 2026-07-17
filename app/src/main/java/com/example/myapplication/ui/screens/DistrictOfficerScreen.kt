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
            MButton(
                text = "REFRESH",
                onClick = { loadDistrictData() }
            )
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
                    DistrictReportCard(report = report)
                }
            }
        }
    }
}

@Composable
fun DistrictReportCard(report: DamageReport) {
    CleanReportCard(
        report = report,
        actions = {
            // Read-only district view — show escalation time
            Spacer(modifier = Modifier.height(8.dp))
            MStripesDivider(height = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Imeletwa na Kijiji: ${report.forwardedAt ?: "Haijulikani"}",
                color = com.example.myapplication.ui.theme.SubtleOnWhite,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    )
}
