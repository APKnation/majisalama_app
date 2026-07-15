package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = ApiClient.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WASIFU WAKO (PROFILE)",
            color = MTextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        MStripesDivider(modifier = Modifier.padding(bottom = 24.dp))

        if (user != null) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MBlueLight,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp)
            )

            MCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileRow(label = "JINA LA MTUMIAJI", value = user.username)
                    ProfileRow(label = "JINA KAMILI", value = user.displayName)
                    ProfileRow(label = "CHEO (ROLE)", value = user.role.replace("_", " ").uppercase())
                    ProfileRow(label = "SIMU", value = user.phone.ifEmpty { "Haijawekwa" })
                    ProfileRow(label = "KIJIJI", value = user.villageName ?: "Hakuna")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        MButton(
            text = "LOGOUT",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MRed.copy(alpha = 0.2f),
            borderColor = MRed,
            contentColor = MRed
        )
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = MTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = value,
            color = MTextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}
