package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myapplication.ui.components.MButton
import com.example.myapplication.ui.components.MStripesDivider
import com.example.myapplication.ui.components.MTextField
import kotlinx.coroutines.launch

@Composable
fun AddVillageScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var populationText by remember { mutableStateOf("") }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
                text = "ONGEZA KIJIJI (ADD VILLAGE)",
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
                    label = "Jina la Kijiji (Village Name)",
                    enabled = !isLoading
                )
            }

            item {
                MTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = "Wilaya (District)",
                    enabled = !isLoading
                )
            }
            
            item {
                MTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = "Mkoa (Region)",
                    enabled = !isLoading
                )
            }
            
            item {
                MTextField(
                    value = populationText,
                    onValueChange = { populationText = it },
                    label = "Idadi ya Watu (Population - Optional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
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
                        text = "ONGEZA KIJIJI (SAVE)",
                        onClick = {
                            if (name.isBlank() || district.isBlank() || region.isBlank()) {
                                errorMessage = "Tafadhali jaza Jina, Wilaya, na Mkoa."
                                return@MButton
                            }

                            val pop = populationText.toIntOrNull() ?: 0
                            val lat = latitudeText.toDoubleOrNull()
                            val lng = longitudeText.toDoubleOrNull()

                            isLoading = true
                            errorMessage = null

                            scope.launch {
                                val res = ApiClient.addVillage(
                                    name = name,
                                    district = district,
                                    region = region,
                                    population = pop,
                                    latitude = lat,
                                    longitude = lng
                                )
                                isLoading = false
                                if (res.isSuccess) {
                                    onSuccess()
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message ?: "Imeshindwa kuongeza kijiji."
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
