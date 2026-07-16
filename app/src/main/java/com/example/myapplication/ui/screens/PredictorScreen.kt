package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.ui.components.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import androidx.compose.material3.MaterialTheme

data class ChatMessage(
    val sender: String, // "bot" or "user"
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictorScreen(
    onNavigateBack: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Chat History
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("bot", "Habari! Mimi ni Msaidizi wa AI wa WaterTrack (WaterTrack AI Assistant)."),
                ChatMessage("bot", "Ninaweza kukusaidia kutabiri mahitaji ya maji kwa wilaya yoyote nchini Tanzania kulingana na data za hali ya hewa na idadi ya watu."),
                ChatMessage("bot", "Tafadhali jaza fomu ya vigezo vya utabiri hapa chini ili nikupe makadirio sahihi.")
            )
        )
    }

    // Input States
    var district by remember { mutableStateOf("Dodoma") }
    val districts = listOf("Dodoma", "Arusha", "Mwanza", "Morogoro", "Mbeya", "Kigamboni", "Kinondoni", "Ilala", "Temeke", "Ubungo")
    var districtExpanded by remember { mutableStateOf(false) }

    var tempText by remember { mutableStateOf("30.0") }
    var rainfallText by remember { mutableStateOf("10.0") }
    var populationText by remember { mutableStateOf("25000") }

    var selectedMonth by remember { mutableStateOf("Jul") }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var monthExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Scroll to bottom helper
    fun scrollToBottom() {
        scope.launch {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
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
                text = "UTABIRI WA AI (PREDICTOR)",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        MStripesDivider(modifier = Modifier.padding(bottom = 16.dp))

        // Chat Display Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { chat ->
                ChatBubble(chatMessage = chat)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WaterTrack AI anafanya utabiri...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guided Parameter Form inputs below the chat
        MCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "VIGEZO VYA UTABIRI (PREDICTION INPUTS)",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // District Selection Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("WILAYA (DISTRICT)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { if (!isLoading) districtExpanded = true }
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = district, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = districtExpanded,
                            onDismissRequest = { districtExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                        ) {
                            districts.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp) },
                                    onClick = {
                                        district = d
                                        districtExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Month Selection Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("MWEZI (MONTH)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { if (!isLoading) monthExpanded = true }
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = selectedMonth, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = monthExpanded,
                            onDismissRequest = { monthExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                        ) {
                            months.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp) },
                                    onClick = {
                                        selectedMonth = m
                                        monthExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("JOTO (°C)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    BasicTextField(
                        value = tempText,
                        onValueChange = { tempText = it },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("MVUA (MM)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    BasicTextField(
                        value = rainfallText,
                        onValueChange = { rainfallText = it },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("POPULATION", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    BasicTextField(
                        value = populationText,
                        onValueChange = { populationText = it },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Submit Button
            MButton(
                text = "OMBA UTABIRI (RUN PREDICTOR)",
                onClick = {
                    val temp = tempText.toDoubleOrNull() ?: 30.0
                    val rain = rainfallText.toDoubleOrNull() ?: 10.0
                    val pop = populationText.toDoubleOrNull() ?: 25000.0

                    // Log user query in chat
                    val userQueryMsg = "Tafadhali tabiri mahitaji ya maji kwa wilaya ya $district, mwezi wa $selectedMonth. Vigezo: Joto=${temp}°C, Mvua=${rain}mm, Idadi ya watu=${populationText}."
                    chatMessages = chatMessages + ChatMessage("user", userQueryMsg)
                    scrollToBottom()

                    isLoading = true
                    scope.launch {
                        val res = ApiClient.predictWaterDemand(district, temp, rain, pop, selectedMonth)
                        isLoading = false
                        if (res.isSuccess) {
                            val demand = res.getOrThrow()
                            val df = DecimalFormat("#,###.##")
                            val botResponse = "Utabiri umekamilika! Mahitaji ya maji yaliyokadiriwa katika wilaya ya $district kwa mwezi wa $selectedMonth ni: \n\n" +
                                    "➜ **${df.format(demand)} Litres kwa mwezi**!\n\n" +
                                    "Hii ni sawa na wastani wa ${df.format(demand / pop / 30.0)} Litres kwa mtu mmoja kwa siku."
                            chatMessages = chatMessages + ChatMessage("bot", botResponse)
                        } else {
                            val errMsg = "Hitilafu imetokea wakati wa kufanya utabiri: " + (res.exceptionOrNull()?.message ?: "Haijulikani")
                            chatMessages = chatMessages + ChatMessage("bot", errMsg)
                        }
                        scrollToBottom()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                borderColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun ChatBubble(chatMessage: ChatMessage) {
    val isBot = chatMessage.sender == "bot"
    val align = if (isBot) Alignment.Start else Alignment.End
    val bgColor = if (isBot) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColors = if (isBot) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isBot) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bgColor, RectangleShape)
                .border(1.dp, borderColors, RectangleShape)
                .padding(10.dp)
        ) {
            Text(
                text = if (isBot) "WATERTRACK AI" else "WEWE (YOU)",
                color = if (isBot) MaterialTheme.colorScheme.secondary else Color.Cyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = chatMessage.message,
                color = textColor,
                fontSize = 13.sp
            )
        }
    }
}
