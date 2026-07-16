package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.data.QualityReport
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

@Composable
fun WaterSourceDetailsScreen(
    sourceId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToReportDamage: (Int) -> Unit,
    onNavigateToLogQuality: (Int) -> Unit
) {
    var source by remember { mutableStateOf<WaterSource?>(null) }
    var qualityReports by remember { mutableStateOf<List<QualityReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val currentUser = ApiClient.currentUser

    LaunchedEffect(sourceId) {
        scope.launch {
            try {
                // Fetch all sources and pick the matching one
                val res = ApiClient.getWaterSources()
                if (res.isSuccess) {
                    source = res.getOrThrow().find { it.id == sourceId }
                } else {
                    errorMessage = res.exceptionOrNull()?.message
                }

                // Fetch quality reports
                val qrRes = ApiClient.getQualityReports(sourceId)
                if (qrRes.isSuccess) {
                    qualityReports = qrRes.getOrThrow()
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
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
            MButton(
                text = "← RUDI (BACK)",
                onClick = onNavigateBack
            )
            Text(
                text = "TAARIFA ZA CHANZO",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
            }
        } else if (source == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = errorMessage ?: "Chanzo cha maji hakijapatikana.",
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            val src = source!!

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basic Info Panel
                item {
                    MCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = src.name.uppercase(),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(status = src.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "AINA YA CHANZO: ${src.sourceTypeDisplay}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(text = "KIJIJI: ${src.villageName}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(text = "MAHALI (GPS): ${src.latitude ?: "N/A"}, ${src.longitude ?: "N/A"}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(text = "MWAKA WA KUJENGWA: ${src.constructionYear ?: "Haijulikani"}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(text = "IMESIMAMIWA NA: ${src.managedByName ?: "N/A"}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }

                // Maintenance Schedule Panel
                item {
                    MCard(borderColor = MaterialTheme.colorScheme.secondary) {
                        Text(
                            text = "RATIBA YA USAFISHAJI / MATENGENEZO",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Usafishaji wa Mwisho: ${src.lastCleaned ?: "Haijafanyika bado"}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(text = "Usafishaji Unaofuata: ${src.nextCleaning ?: "Haipangiliwa bado"}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }

                // Water quality parameters
                item {
                    MCard(borderColor = if (src.status == "safe") Color(0xFF4CAF50) else Color(0xFFF44336)) {
                        Text(
                            text = "UBORA WA MAJI (VIPIMO VYA SASA)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (src.phLevel == null) {
                            Text(
                                text = "Hakuna data za vipimo zilizorekodiwa hivi karibuni.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParameterDisplay("pH Level", src.phLevel.toString(), "6.5 - 8.5", src.phLevel in 6.5..8.5)
                                ParameterDisplay("Bacteria", "${src.bacteriaCount} CFU", "≤ 10 CFU", (src.bacteriaCount ?: 0) <= 10)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParameterDisplay("Iron (Chuma)", "${src.ironLevel} mg/L", "≤ 0.3 mg/L", (src.ironLevel ?: 0.0) <= 0.3)
                                ParameterDisplay("Turbidity (Fukuto)", "${src.turbidity} NTU", "≤ 5 NTU", (src.turbidity ?: 0.0) <= 5.0)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Vipimo vya mwisho: ${src.lastTested ?: "N/A"}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Action Buttons
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MButton(
                            text = "RIPOTI UHARIBIFU KANZA HAPA",
                            onClick = { onNavigateToReportDamage(src.id) },
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.error
                        )

                        // If water officer, log inspection
                        if (currentUser?.role == "water_officer" || currentUser?.role == "admin") {
                            MButton(
                                text = "PIMA NA UWEKE UBORA WA MAJI (INSPECTION)",
                                onClick = { onNavigateToLogQuality(src.id) },
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = Color(0xFF4CAF50),
                                contentColor = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                // Inspection history Header
                item {
                    Text(
                        text = "HISTORIA YA VIPIMO VYA MAJI",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (qualityReports.isEmpty()) {
                    item {
                        Text(
                            text = "Hakuna vipimo vilivyowahi kurekodiwa vya nyuma.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    items(qualityReports) { report ->
                        QualityReportItem(report = report)
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterDisplay(
    title: String,
    value: String,
    range: String,
    isSafe: Boolean
) {
    val color = if (isSafe) Color(0xFF4CAF50) else Color(0xFFF44336)
    Column(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .width(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title.uppercase(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Kawaida: $range", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
    }
}

@Composable
fun QualityReportItem(report: QualityReport) {
    MCard(borderColor = MaterialTheme.colorScheme.outline) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TAREHE: ${report.testDate}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Mtaalamu: ${report.testedByName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = "pH: ${report.phLevel} | Bact: ${report.bacteriaCount} | Iron: ${report.ironLevel}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (report.notes.isNotEmpty()) {
                    Text(
                        text = "Maelezo: ${report.notes}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            StatusBadge(status = if (report.isSafe) "safe" else "unsafe")
        }
    }
}
