package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    data class WaterSourceDetails(val sourceId: Int) : Screen()
    data class ReportDamage(val sourceId: Int?) : Screen()
    object VillageLeaderPanel : Screen()
    object WaterOfficerPanel : Screen()
    data class LogQuality(val sourceId: Int) : Screen()
    object Predictor : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(
                darkTheme = true,
                dynamicColor = false
            ) {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    // Custom Stack Navigation
    val backStack = remember { mutableStateListOf<Screen>(Screen.Login) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Login

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    fun navigateToRoot(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    // Handle android physical back button
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "WATERTRACK",
                            color = MTextWhite,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    },
                    actions = {
                        if (ApiClient.accessToken != null) {
                            IconButton(onClick = {
                                ApiClient.logout()
                                navigateToRoot(Screen.Login)
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = MRed
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MBlack,
                        titleContentColor = MTextWhite
                    )
                )
                // Distinct tricolor BMW M band directly under the TopBar
                MStripesDivider(height = 2.dp)
            }
        },
        bottomBar = {
            if (ApiClient.accessToken != null) {
                Column {
                    MStripesDivider(height = 1.dp)
                    NavigationBar(
                        containerColor = MBlack,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(72.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.Dashboard,
                            onClick = { navigateToRoot(Screen.Dashboard) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                            label = { Text("Home", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MTextWhite,
                                selectedTextColor = MTextWhite,
                                indicatorColor = MBlueDark.copy(alpha = 0.4f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Predictor,
                            onClick = { navigateToRoot(Screen.Predictor) },
                            icon = { Icon(Icons.Default.Info, contentDescription = "AI Predictor") },
                            label = { Text("AI Predictor", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MTextWhite,
                                selectedTextColor = MTextWhite,
                                indicatorColor = MBlueLight.copy(alpha = 0.4f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
                    }
                }
            }
        },
        containerColor = MBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MBlack)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Login -> {
                    LoginScreen(
                        onLoginSuccess = {
                            navigateToRoot(Screen.Dashboard)
                        },
                        onNavigateToRegister = {
                            navigateTo(Screen.Register)
                        }
                    )
                }
                is Screen.Register -> {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navigateToRoot(Screen.Dashboard)
                        },
                        onNavigateToLogin = {
                            navigateBack()
                        }
                    )
                }
                is Screen.Dashboard -> {
                    DashboardScreen(
                        onNavigateToSourceDetails = { id ->
                            navigateTo(Screen.WaterSourceDetails(id))
                        },
                        onNavigateToReportDamage = { initialId ->
                            navigateTo(Screen.ReportDamage(initialId))
                        },
                        onNavigateToPredictor = {
                            navigateTo(Screen.Predictor)
                        },
                        onNavigateToLeaderPanel = {
                            navigateTo(Screen.VillageLeaderPanel)
                        },
                        onNavigateToOfficerPanel = {
                            navigateTo(Screen.WaterOfficerPanel)
                        }
                    )
                }
                is Screen.WaterSourceDetails -> {
                    WaterSourceDetailsScreen(
                        sourceId = currentScreen.sourceId,
                        onNavigateBack = {
                            navigateBack()
                        },
                        onNavigateToReportDamage = { id ->
                            navigateTo(Screen.ReportDamage(id))
                        },
                        onNavigateToLogQuality = { id ->
                            navigateTo(Screen.LogQuality(id))
                        }
                    )
                }
                is Screen.ReportDamage -> {
                    ReportDamageScreen(
                        initialSourceId = currentScreen.sourceId,
                        onNavigateBack = {
                            navigateBack()
                        },
                        onSuccess = {
                            navigateBack()
                        }
                    )
                }
                is Screen.VillageLeaderPanel -> {
                    VillageLeaderScreen(
                        onNavigateBack = {
                            navigateBack()
                        }
                    )
                }
                is Screen.WaterOfficerPanel -> {
                    WaterOfficerScreen(
                        onNavigateBack = {
                            navigateBack()
                        }
                    )
                }
                is Screen.LogQuality -> {
                    LogQualityScreen(
                        waterSourceId = currentScreen.sourceId,
                        onNavigateBack = {
                            navigateBack()
                        },
                        onSuccess = {
                            navigateBack()
                        }
                    )
                }
                is Screen.Predictor -> {
                    PredictorScreen(
                        onNavigateBack = {
                            navigateToRoot(Screen.Dashboard)
                        }
                    )
                }
            }
        }
    }
}