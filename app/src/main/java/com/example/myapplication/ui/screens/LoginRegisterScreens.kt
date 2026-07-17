package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.User
import com.example.myapplication.data.Village
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

// ─── Default Demo Credentials ───────────────────────────────────────────────
// These match the Django seeded / superuser accounts.
// Remove or replace before production release.
private data class DemoAccount(
    val label: String,
    val username: String,
    val password: String,
    val color: androidx.compose.ui.graphics.Color
)

private val demoAccounts = listOf(
    DemoAccount("ADMIN",          "admin",          "admin123",   androidx.compose.ui.graphics.Color(0xFFE22718)),
    DemoAccount("CITIZEN",        "citizen1",       "pass1234",   androidx.compose.ui.graphics.Color(0xFF4CAF50)),
    DemoAccount("LEADER",         "leader1",        "pass1234",   androidx.compose.ui.graphics.Color(0xFF1C69D4)),
    DemoAccount("WATER OFFICER",  "officer1",       "pass1234",   androidx.compose.ui.graphics.Color(0xFF0066B1))
)
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Pre-fill with admin defaults so testers can log in immediately
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top-left Home Button
        IconButton(
            onClick = onNavigateToHome,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Back to Home",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Headline
            Text(
                text = "WATERTRACK",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "TANZANIA WATER PORTAL",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            MStripesDivider(modifier = Modifier.padding(bottom = 12.dp))

            // ── Demo Quick-Login Buttons ─────────────────────────────────
            Text(
                text = "DEMO ACCOUNTS (SKIP TYPING)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                demoAccounts.forEach { demo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, demo.color, RoundedCornerShape(16.dp))
                            .background(demo.color.copy(alpha = 0.1f))
                            .clickable(enabled = !isLoading) {
                                username = demo.username
                                password = demo.password
                                errorMessage = null
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = demo.label,
                            color = demo.color,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            // ─────────────────────────────────────────────────────────────

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            MTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username au Email",
                modifier = Modifier.padding(bottom = 16.dp),
                enabled = !isLoading
            )

            MTextField(
                value = password,
                onValueChange = { password = it },
                label = "Neno la Siri (Password)",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.padding(bottom = 24.dp),
                enabled = !isLoading
            )

            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                MButton(
                    text = "INGIA (LOGIN)",
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Tafadhali jaza nafasi zote."
                            return@MButton
                        }
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val result = ApiClient.login(username, password)
                            isLoading = false
                            if (result.isSuccess) {
                                onLoginSuccess(result.getOrThrow())
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Kuingia kumeshindwa."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hauna akaunti? Jisajili hapa",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .padding(8.dp)
                )

                Text(
                    text = "Rudi Nyumbani (Back to Home)",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clickable { onNavigateToHome() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    // Dropdown States
    var selectedRole by remember { mutableStateOf("citizen") }
    val roles = listOf(
        Pair("citizen", "Mwananchi (Citizen)"),
        Pair("village_leader", "Kiongozi wa Kijiji (Leader)"),
        Pair("water_officer", "Afisa wa Maji (Water Officer)"),
        Pair("district_officer", "Ofisa wa Wilaya (District Officer)")
    )
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    var villages by remember { mutableStateOf<List<Village>>(emptyList()) }
    var selectedVillage by remember { mutableStateOf<Village?>(null) }
    var villageDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch villages on load
    LaunchedEffect(Unit) {
        val res = ApiClient.getVillages()
        if (res.isSuccess) {
            val list = res.getOrThrow()
            villages = list
            if (list.isNotEmpty()) {
                selectedVillage = list.first()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top-left Home Button
        IconButton(
            onClick = onNavigateToHome,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Back to Home",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "JISAJILI (REGISTER)",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Monospace
                )
                MStripesDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                MTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "Jina la Kwanza",
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Jina la Ukoo",
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Namba ya Simu",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !isLoading
                )
            }

            // Role Selector
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "JUKUMU (ROLE)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { if (!isLoading) roleDropdownExpanded = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = roles.find { it.first == selectedRole }?.second ?: selectedRole,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        ) {
                            roles.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.second, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        selectedRole = r.first
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Village Selector
            if (villages.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "KIJIJI (VILLAGE)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .clickable { if (!isLoading) villageDropdownExpanded = true }
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedVillage?.name ?: "Chagua Kijiji",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            DropdownMenu(
                                expanded = villageDropdownExpanded,
                                onDismissRequest = { villageDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            ) {
                                villages.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text("${v.name} (${v.district})", color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedVillage = v
                                            villageDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    MButton(
                        text = "JISAJILI SASA",
                        onClick = {
                            if (username.isBlank() || email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
                                errorMessage = "Tafadhali jaza taarifa zote muhimu."
                                return@MButton
                            }
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                val result = ApiClient.register(
                                    username = username,
                                    email = email,
                                    securePassword = password,
                                    firstName = firstName,
                                    lastName = lastName,
                                    role = selectedRole,
                                    phone = phone,
                                    villageId = selectedVillage?.id
                                )
                                isLoading = false
                                if (result.isSuccess) {
                                    onRegisterSuccess(result.getOrThrow())
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Jisajili kumeshindwa."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tayari una akaunti? Ingia hapa",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                            .padding(8.dp)
                    )

                    Text(
                        text = "Rudi Nyumbani (Back to Home)",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { onNavigateToHome() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
