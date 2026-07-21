package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.Village
import com.example.myapplication.ui.components.MButton
import com.example.myapplication.ui.components.MStripesDivider
import com.example.myapplication.ui.components.MTextField
import kotlinx.coroutines.launch

@Composable
fun AddWaterSourceScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    // Source Type Dropdown
    val sourceTypes = listOf(
        Pair("shallow_well", "Kisima cha Juu (Shallow Well)"),
        Pair("deep_well", "Kisima cha Kina (Deep Well)"),
        Pair("spring", "Chemchem (Spring)"),
        Pair("river", "Mto (River)"),
        Pair("dam", "Bwawa (Dam)"),
        Pair("borehole", "Bomba la Kuchimba (Borehole)"),
        Pair("rainwater", "Maji ya Mvua (Rainwater)")
    )
    var selectedSourceType by remember { mutableStateOf(sourceTypes.first().first) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Village Dropdown
    var villages by remember { mutableStateOf<List<Village>>(emptyList()) }
    var selectedVillage by remember { mutableStateOf<Village?>(null) }
    var villageDropdownExpanded by remember { mutableStateOf(false) }

    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
                text = "← BACK",
                onClick = onNavigateBack
            )
            Text(
                text = "ONGEZA CHANZO (ADD SOURCE)",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    value = name,
                    onValueChange = { name = it },
                    label = "Jina la Chanzo (Source Name)",
                    enabled = !isLoading
                )
            }

            // Source Type Selector
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "AINA YA CHANZO (SOURCE TYPE)",
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
                            .clickable { if (!isLoading) typeDropdownExpanded = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = sourceTypes.find { it.first == selectedSourceType }?.second ?: selectedSourceType,
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
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        ) {
                            sourceTypes.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.second, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        selectedSourceType = t.first
                                        typeDropdownExpanded = false
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
            } else {
                item {
                    Text(
                        text = "Inapakia vijiji...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                MTextField(
                    value = latitudeText,
                    onValueChange = { latitudeText = it },
                    label = "Latitude (Sio lazima / Optional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = longitudeText,
                    onValueChange = { longitudeText = it },
                    label = "Longitude (Sio lazima / Optional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                    }
                } else {
                    MButton(
                        text = "ONGEZA CHANZO (SAVE)",
                        onClick = {
                            if (name.isBlank() || selectedVillage == null) {
                                errorMessage = "Tafadhali jaza jina la chanzo na uchague kijiji."
                                return@MButton
                            }

                            val lat = latitudeText.toDoubleOrNull()
                            val lng = longitudeText.toDoubleOrNull()

                            isLoading = true
                            errorMessage = null

                            scope.launch {
                                val res = ApiClient.addWaterSource(
                                    name = name,
                                    sourceType = selectedSourceType,
                                    villageId = selectedVillage!!.id,
                                    villageName = selectedVillage!!.name,
                                    latitude = lat,
                                    longitude = lng
                                )
                                isLoading = false
                                if (res.isSuccess) {
                                    onSuccess()
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message ?: "Imeshindwa kuongeza chanzo."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
