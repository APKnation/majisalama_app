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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.WaterSource
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*
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
    
    // SweetAlert dialog state
    var sweetAlertData by remember { mutableStateOf<SweetAlertData?>(null) }
    
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

    Box(modifier = Modifier.fillMaxSize()) {
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
                        Text(
                            text = "CHANZO CHA MAJI (WATER SOURCE)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(enabled = initialSourceId == null && sources.isNotEmpty()) {
                                    sourceDropdownExpanded = true
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedSource?.name ?: "Chagua Chanzo Cha Maji",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (initialSourceId == null && sources.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = sourceDropdownExpanded,
                                onDismissRequest = { sourceDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            ) {
                                sources.forEach { src ->
                                    DropdownMenuItem(
                                        text = { Text(src.name, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedSource = src
                                            sourceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Title
                    item {
                        MTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Kichwa cha Ripoti (Title)"
                        )
                    }

                    // Description
                    item {
                        MTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Maelezo ya Uharibifu (Description)"
                        )
                    }

                    // Priority Selector
                    item {
                        Text(
                            text = "KIPAUMBELE (PRIORITY)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { priorityDropdownExpanded = true }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val currentLabel = priorities.find { it.first == selectedPriority }?.second ?: ""
                                Text(
                                    text = currentLabel,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
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
                                    .fillMaxWidth(0.9f)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            ) {
                                priorities.forEach { (key, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedPriority = key
                                            priorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
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
                                        sweetAlertData = SweetAlertData(
                                            title = "Taarifa Inahitajika",
                                            message = "Tafadhali chagua chanzo cha maji kabla ya kutuma ripoti.",
                                            type = SweetAlertType.WARNING
                                        )
                                        return@MButton
                                    }
                                    if (title.isBlank() || description.isBlank()) {
                                        sweetAlertData = SweetAlertData(
                                            title = "Taarifa Inahitajika",
                                            message = "Tafadhali jaza kichwa cha ripoti pamoja na maelezo ya uharibifu.",
                                            type = SweetAlertType.WARNING
                                        )
                                        return@MButton
                                    }

                                    sweetAlertData = SweetAlertData(
                                        title = "Thibitisha Kutuma Ripoti",
                                        message = "Je, una uhakika unataka kutuma ripoti hii ya uharibifu wa mfumo wa maji?",
                                        type = SweetAlertType.CONFIRM,
                                        confirmButtonText = "Ndio, Tuma",
                                        cancelButtonText = "Ghairi",
                                        onConfirm = {
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
                                                    sweetAlertData = SweetAlertData(
                                                        title = "Ripoti Imetumwa!",
                                                        message = "Ripoti yako imetolewa kikamilifu. Utaweza kufuatilia maendeleo ya ukarabati.",
                                                        type = SweetAlertType.SUCCESS,
                                                        confirmButtonText = "Sawa",
                                                        onConfirm = { onSuccess() }
                                                    )
                                                } else {
                                                    sweetAlertData = SweetAlertData(
                                                        title = "Imeshindwa",
                                                        message = res.exceptionOrNull()?.message ?: "Imeshindwa kutuma ripoti. Jaribu tena.",
                                                        type = SweetAlertType.ERROR
                                                    )
                                                }
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = BlueOcean,
                                contentColor = WhitePure,
                                backgroundColor = BlueOcean
                            )
                        }
                    }
                }
            }
        }

        sweetAlertData?.let { data ->
            SweetAlertDialog(
                data = data,
                onDismissRequest = { sweetAlertData = null }
            )
        }
    }
}
