package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.*

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Hero Section ──────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MDarkGray)
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MStripesDivider(height = 4.dp, modifier = Modifier.padding(bottom = 24.dp))

                Text(
                    text = "MAJI",
                    color = MTextWhite,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SALAMA",
                    color = MBlueLight,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Mfumo wa kisasa wa ufuatiliaji wa\nrasilimali za maji Tanzania",
                    color = MTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // CTA Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MButton(
                        text = "INGIA (LOGIN)",
                        onClick = onNavigateToLogin,
                        modifier = Modifier.weight(1f)
                    )
                    MButton(
                        text = "JIANDIKISHE",
                        onClick = onNavigateToRegister,
                        modifier = Modifier.weight(1f),
                        backgroundColor = MBlueDark.copy(alpha = 0.2f),
                        borderColor = MBlueDark
                    )
                }

                MStripesDivider(height = 4.dp, modifier = Modifier.padding(top = 24.dp))
            }
        }

        // ── Stats Banner ──────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(value = "500+", label = "VYANZO VYA MAJI", color = MBlueLight, modifier = Modifier.weight(1f))
                StatBox(value = "120", label = "VIJIJI", color = Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                StatBox(value = "3", label = "MIKOA", color = MRed, modifier = Modifier.weight(1f))
            }
        }

        // ── Features Section ──────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "HUDUMA ZETU",
                    color = MTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                MStripesDivider(height = 2.dp, modifier = Modifier.padding(bottom = 8.dp))

                FeatureCard(
                    icon = Icons.Default.Star,
                    title = "UFUATILIAJI WA UBORA",
                    description = "Fuatilia hali ya ubora wa maji katika vyanzo vyote kwa wakati halisi.",
                    iconColor = MBlueLight
                )
                FeatureCard(
                    icon = Icons.Default.Notifications,
                    title = "RIPOTI ZA HARAKA",
                    description = "Ripoti uharibifu wa maji na ufuatiliaji wao hadi usuluhishaji.",
                    iconColor = MRed
                )
                FeatureCard(
                    icon = Icons.Default.Info,
                    title = "AI PREDICTOR",
                    description = "Tumia akili bandia kutabiri mahitaji ya maji katika eneo lako.",
                    iconColor = Color(0xFF4CAF50)
                )
            }
        }

        // ── Roles Section ─────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "WATUMIAJI",
                    color = MTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                MStripesDivider(height = 2.dp, modifier = Modifier.padding(bottom = 8.dp))

                RoleRow(role = "MWANANCHI", desc = "Angalia hali ya maji na ripoti uharibifu", color = MTextWhite)
                RoleRow(role = "KIONGOZI WA KIJIJI", desc = "Simamia na uidhinishe ripoti za kijiji", color = MBlueDark)
                RoleRow(role = "AFISA WA MAJI", desc = "Tekeleza kazi na ukaguzi wa ubora", color = MBlueLight)
                RoleRow(role = "AFISA WA WILAYA", desc = "Simamia ripoti zilizopandishwa wilaya", color = MRed)
                RoleRow(role = "MSIMAMIZI", desc = "Udhibiti kamili wa mfumo wote", color = Color(0xFFFFB300))
            }
        }

        // ── Bottom CTA ────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MBorderGray, RectangleShape)
                    .background(MDarkGray)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ANZA LEO",
                    color = MTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Jiunge na mfumo wa kisasa wa\nusimamizi wa maji nchini Tanzania",
                    color = MTextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                MButton(
                    text = "JIANDIKISHE SASA",
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                )
                MButton(
                    text = "NINA AKAUNTI → INGIA",
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MBlack,
                    borderColor = MBorderGray,
                    contentColor = MTextMuted
                )
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.5f), RectangleShape)
            .background(color.copy(alpha = 0.08f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            Text(label, color = MTextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, description: String, iconColor: Color) {
    MCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(32.dp))
            Column {
                Text(title, color = MTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = MTextMuted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun RoleRow(role: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.4f), RectangleShape)
            .background(color.copy(alpha = 0.07f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(role, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            Text(desc, color = MTextMuted, fontSize = 11.sp)
        }
    }
}
