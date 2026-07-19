package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.myapplication.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.DamageReport
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToReportDamage: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var recentReports by remember { mutableStateOf<List<DamageReport>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            val reportResult = ApiClient.getDamageReports()
            if (reportResult.isSuccess) recentReports = reportResult.getOrThrow()
        }
    }

    // Root column: header + scrollable body + pinned bottom panel
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueMist)
    ) {
        // ── Custom Top Bar ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueAbyss)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = WhitePure,
                modifier = Modifier.size(26.dp).clickable { onNavigateToLogin() }
            )
            Text(
                text = "MajiSalama",
                color = WhitePure,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Login",
                tint = WhitePure,
                modifier = Modifier.size(26.dp).clickable { onNavigateToLogin() }
            )
        }

        // ── Scrollable Main Content ────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),  // Takes all space except the pinned bottom panel
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    // Full-width photo banner (tank1.png from res/drawable/)
                    Image(
                        painter = painterResource(id = R.drawable.tank1),
                        contentDescription = "Water tank banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay so the text stays readable
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BlueAbyss.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )
                    // Tagline centered on the image
                    Text(
                        text = "Tunza Maji, Tunza Uhai.",
                        color = WhitePure,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairDisplay,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp)
                    )
                }
            }

            // 2-Column Action Grid
            item {
                val actions = listOf(
                    ActionItem("Ripoti", "Ripoti uharibifu sasa", Icons.Default.ReportProblem),
                    ActionItem("Vyanzo", "Tafuta chanzo karibu", Icons.Default.LocationOn),
                    ActionItem("Taarifa", "Ripoti za umma", Icons.AutoMirrored.Filled.ListAlt),
                    ActionItem("Ripoti Zangu", "Taarifa za uharibifu", Icons.AutoMirrored.Filled.ListAlt),
                    ActionItem("Maoni", "Toa maoni yako!", Icons.Default.ThumbUp),
                    ActionItem("Mipangilio", "Sanidi mipangilio", Icons.Default.Settings)
                )
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    actions.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            row.forEach { action ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(105.dp)
                                        .clickable { 
                                            if (action.title == "Ripoti") onNavigateToReportDamage() 
                                            else onNavigateToLogin() 
                                        },
                                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = action.title,
                                            tint = BlueAbyss,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = action.title,
                                            color = BlueNight,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = action.subtitle,
                                            color = SubtleOnWhite,
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
        }

        // ── "How It Works" Feature Section (tank2.png) ───────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                // tank2.png — community water infrastructure photo
                Image(
                    painter = painterResource(id = R.drawable.tank2),
                    contentDescription = "Wananchi wakitumia huduma ya maji safi",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Full gradient overlay: transparent at top → deep teal at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color(0xFF003049).copy(alpha = 0.30f),
                                    0.45f to Color(0xFF005F73).copy(alpha = 0.65f),
                                    1.0f to Color(0xFF001D2E).copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                // Content overlaid on the image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    // Section badge
                    Surface(
                        color = ButtonAccent.copy(alpha = 0.90f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "  JINSI INAVYOFANYA KAZI  ",
                            color = WhitePure,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Headline
                    Text(
                        text = "Angalia, Ripoti, Fuatilia",
                        color = WhitePure,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(Modifier.height(6.dp))

                    // Description
                    Text(
                        text = "Piga picha ya uharibifu wa mfumo wa maji, tuma ripoti moja kwa moja kupitia programu hii, na fuatilia maendeleo hadi tatizo lishughulikiwe.",
                        color = WhitePure.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // CTA Button
                    Button(
                        onClick = onNavigateToReportDamage,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonAccent),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = WhitePure
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ripoti Uharibifu Sasa",
                            color = WhitePure,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // ── Recent Reports Header ──────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = BlueOcean,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Ripoti za Hivi Karibuni",
                        color = BlueNight,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "Ingia ili kuona zaidi →",
                    color = BlueOcean,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }

        // ── Recent Reports List ───────────────────────────────────────────────
        if (recentReports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hakuna ripoti bado",
                        color = SubtleOnWhite,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            items(recentReports) { report ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    com.example.myapplication.ui.components.CleanReportCard(
                        report = report,
                        onClick = onNavigateToLogin
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
}

data class ActionItem(val title: String, val subtitle: String, val icon: ImageVector)
