package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.ui.components.*

@Composable
fun AdminScreen(
    onNavigateToCitizen: () -> Unit,
    onNavigateToLeader: () -> Unit,
    onNavigateToOfficer: () -> Unit,
    onNavigateToDistrict: () -> Unit,
    onNavigateToPredictor: () -> Unit
) {
    val user = ApiClient.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ADMIN MASTER DASHBOARD",
                    color = MTextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Karibu, ${user?.displayName?.uppercase() ?: "ADMIN"}",
                    color = MRed,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                MStripesDivider()
            }
        }

        item {
            Text(
                text = "CHAGUA PANELI (SELECT PANEL)",
                color = MTextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            MButton(
                text = "→ DASHBOARD YA MWANANCHI (CITIZEN)",
                onClick = onNavigateToCitizen,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MBlack,
                borderColor = MTextWhite,
                contentColor = MTextWhite
            )
        }

        item {
            MButton(
                text = "→ DASHBOARD YA KIONGOZI (LEADER)",
                onClick = onNavigateToLeader,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MBlueDark.copy(alpha = 0.2f),
                borderColor = MBlueDark
            )
        }

        item {
            MButton(
                text = "→ DASHBOARD YA AFISA WA MAJI (OFFICER)",
                onClick = onNavigateToOfficer,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MBlueLight.copy(alpha = 0.2f),
                borderColor = MBlueLight
            )
        }

        item {
            MButton(
                text = "→ DASHBOARD YA WILAYA (DISTRICT)",
                onClick = onNavigateToDistrict,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MRed.copy(alpha = 0.2f),
                borderColor = MRed
            )
        }

        item {
            MStripesDivider(height = 1.dp)
        }

        item {
            MButton(
                text = "AI PREDICTOR",
                onClick = onNavigateToPredictor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
