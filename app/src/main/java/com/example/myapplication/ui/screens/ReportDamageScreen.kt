package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDamageScreen(
    initialSourceId: Int?,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("medium") }
    val priorities = listOf(
        Pair("low", "Ndogo (Low)"),
        Pair("medium", "Wastani (Medium)"),
        Pair("high", "Kubwa (High)"),
        Pair("critical", "Dharura (Critical)")
    )
    var priorityDropdownExpanded by remember { mutableStateOf(false) }

    var sources by remember { mutableStateOf<List<WaterSource>>(emptyList()) }
    var selectedSource by remember { mutableStateOf<WaterSource?>(null) }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var isFetchingSources by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch water sources on load if initialSourceId is null or to set initial value
    LaunchedEffect(Unit) {
        isFetchingSources = true
        val res = ApiClient.getWaterSources()
        isFetchingSources = false
        if (res.isSuccess) {
            val list = res.getOrThrow()
            sources = list
            if (initialSourceId != null) {
                selectedSource = list.find { it.id == initialSourceId }
            } else if (list.isNotEmpty()) {
                selectedSource = list.first()
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
                text = "← GHAIRI (CANCEL)",
                onClick = onNavigateBack
            )
            Text(
                text = "RIPOTI UHARIBIFU",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        if (isFetchingSources) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error Display
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

                // Water Source Selector / Lock Display
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CHANZO CHA MAJI (WATER SOURCE)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        if (initialSourceId != null && selectedSource != null) {
                            // Locked display since we came from Detail Screen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = selectedSource!!.name.uppercase(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            // Dropdown selection
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                                    .background(MaterialTheme.colorScheme.background)
                                    .clickable { if (!isLoading) sourceDropdownExpanded = true }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedSource?.name ?: "Chagua Chanzo",
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
                                    expanded = sourceDropdownExpanded,
                                    onDismissRequest = { sourceDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                                ) {
                                    sources.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text("${s.name} (${s.villageName})", color = MaterialTheme.colorScheme.onSurface) },
                                            onClick = {
                                                selectedSource = s
                                                sourceDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Priority Selection
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "DHARURA / UMUHIMU (PRIORITY)",
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
                                .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .clickable { if (!isLoading) priorityDropdownExpanded = true }
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = priorities.find { it.first == selectedPriority }?.second ?: selectedPriority,
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
                                expanded = priorityDropdownExpanded,
                                onDismissRequest = { priorityDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            ) {
                                priorities.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.second, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedPriority = p.first
                                            priorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Title Input
                item {
                    MTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Kichwa cha Ripoti (Title)",
                        enabled = !isLoading
                    )
                }

                // Description Input
                item {
                    MTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Maelezo ya Uharibifu (Description)",
                        singleLine = false,
                        enabled = !isLoading,
                        modifier = Modifier.height(140.dp)
                    )
                }

                // Submit Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                        }
                    } else {
                        MButton(
                            text = "TUMA RIPOTI (SUBMIT REPORT)",
                            onClick = {
                                if (selectedSource == null) {
                                    errorMessage = "Tafadhali chagua chanzo cha maji."
                                    return@MButton
                                }
                                if (title.isBlank() || description.isBlank()) {
                                    errorMessage = "Tafadhali jaza kichwa na maelezo."
                                    return@MButton
                                }
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    val res = ApiClient.reportDamage(
                                        waterSourceId = selectedSource!!.id,
                                        title = title,
                                        description = description,
                                        priority = selectedPriority
                                    )
                                    isLoading = false
                                    if (res.isSuccess) {
                                        onSuccess()
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "Imeshindwa kutuma ripoti."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
