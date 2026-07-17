package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.ui.components.CleanReportCard
import com.example.myapplication.ui.components.MStripesDivider
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TaarfiaScreen(
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var reports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Zote") }

    val filters = listOf("Zote", "Inasubiri", "Inafanyiwa", "Imetatuliwa")

    LaunchedEffect(Unit) {
        scope.launch {
            val result = ApiClient.getDamageReports()
            if (result.isSuccess) reports = result.getOrThrow()
            isLoading = false
        }
    }

    val filteredReports = when (selectedFilter) {
        "Inasubiri"   -> reports.filter { it.status.contains("pending") }
        "Inafanyiwa"  -> reports.filter { it.status == "assigned" || it.status == "in_progress" }
        "Imetatuliwa" -> reports.filter { it.status == "resolved" || it.status == "closed" }
        else          -> reports
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueMist)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueAbyss)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Taarifa za Uharibifu",
                color = WhitePure,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${reports.size} ripoti zimepatikana • Ingia ili kuripoti",
                color = WhitePure.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ── Filter Chips ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhitePure)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BlueAbyss,
                        selectedLabelColor = WhitePure,
                        containerColor = BlueMist,
                        labelColor = BlueNight
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = BlueIce,
                        selectedBorderColor = BlueAbyss,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        MStripesDivider(height = 2.dp)

        // ── Report List ───────────────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueAbyss)
            }
        } else if (filteredReports.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = BlueIce,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Hakuna ripoti katika kundi hili",
                        color = SubtleOnWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredReports) { report ->
                    CleanReportCard(
                        report = report,
                        onClick = onNavigateToLogin
                    )
                }

                item {
                    // Login CTA at the bottom
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlueAbyss),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = WhitePure.copy(alpha = 0.7f),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Ingia ili kuripoti uharibifu au kufuatilia kwa undani",
                                color = WhitePure,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToLogin,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WhitePure,
                                    contentColor = BlueAbyss
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Ingia",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
