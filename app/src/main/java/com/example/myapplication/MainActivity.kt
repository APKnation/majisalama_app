package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ApiClient
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Landing : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    data class WaterSourceDetails(val sourceId: Int) : Screen()
    data class ReportDamage(val sourceId: Int?) : Screen()
    object VillageLeaderPanel : Screen()
    object WaterOfficerPanel : Screen()
    object DistrictOfficerPanel : Screen()
    object AdminPanel : Screen()
    data class LogQuality(val sourceId: Int) : Screen()
    object Predictor : Screen()
    object Profile : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                MainAppContainer()
            }
        }
    }
}

// ── Drawer nav item data ─────────────────────────────────────────────────────
private data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val screen: Screen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Landing) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Landing
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun navigateTo(screen: Screen) { backStack.add(screen) }
    fun navigateBack() { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
    fun navigateToRoot(screen: Screen) { backStack.clear(); backStack.add(screen) }
    fun closeDrawer() { scope.launch { drawerState.close() } }

    fun getRoleHomeRoute(): Screen {
        val user = ApiClient.currentUser ?: return Screen.Login
        return when (user.role) {
            "village_leader"   -> Screen.VillageLeaderPanel
            "water_officer"    -> Screen.WaterOfficerPanel
            "district_officer" -> Screen.DistrictOfficerPanel
            "admin"            -> Screen.AdminPanel
            else               -> Screen.Dashboard
        }
    }

    BackHandler(enabled = backStack.size > 1) { navigateBack() }

    // ── Screens where top/bottom chrome is hidden (full screen) ────────────
    val isFullScreen = currentScreen is Screen.Landing ||
                       currentScreen is Screen.Login   ||
                       currentScreen is Screen.Register

    // ── Screens where we show a back arrow instead of hamburger ────────────
    val isRootScreen = currentScreen is Screen.Dashboard ||
                       currentScreen is Screen.AdminPanel ||
                       currentScreen is Screen.VillageLeaderPanel ||
                       currentScreen is Screen.WaterOfficerPanel ||
                       currentScreen is Screen.DistrictOfficerPanel ||
                       currentScreen is Screen.Profile ||
                       currentScreen is Screen.Predictor

    // ── Build role-appropriate drawer items ────────────────────────────────
    val user = ApiClient.currentUser
    val drawerItems: List<DrawerItem> = buildList {
        when (user?.role) {
            "admin" -> {
                add(DrawerItem(Icons.Default.Dashboard, "Admin Dashboard", Screen.AdminPanel))
                add(DrawerItem(Icons.Default.People, "Citizen View", Screen.Dashboard))
                add(DrawerItem(Icons.Default.Groups, "Leader Panel", Screen.VillageLeaderPanel))
                add(DrawerItem(Icons.Default.Engineering, "Officer Panel", Screen.WaterOfficerPanel))
                add(DrawerItem(Icons.Default.LocationCity, "District Panel", Screen.DistrictOfficerPanel))
            }
            "village_leader" -> {
                add(DrawerItem(Icons.Default.Dashboard, "Leader Dashboard", Screen.VillageLeaderPanel))
                add(DrawerItem(Icons.Default.WaterDrop, "Vyanzo vya Maji", Screen.Dashboard))
            }
            "water_officer" -> {
                add(DrawerItem(Icons.Default.Dashboard, "Officer Dashboard", Screen.WaterOfficerPanel))
                add(DrawerItem(Icons.Default.WaterDrop, "Vyanzo vya Maji", Screen.Dashboard))
            }
            "district_officer" -> {
                add(DrawerItem(Icons.Default.Dashboard, "District Dashboard", Screen.DistrictOfficerPanel))
            }
            else -> {  // citizen
                add(DrawerItem(Icons.Default.Home, "Nyumbani", Screen.Dashboard))
            }
        }
        add(DrawerItem(Icons.Default.AutoAwesome, "AI Predictor", Screen.Predictor))
        add(DrawerItem(Icons.Default.ReportProblem, "Ripoti Uharibifu", Screen.ReportDamage(null)))
        add(DrawerItem(Icons.Default.Person, "Wasifu Wangu", Screen.Profile))
    }

    // ── Wrap everything in ModalNavigationDrawer ───────────────────────────
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isFullScreen,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                drawerContainerColor = MSurface,
                drawerTonalElevation = 0.dp
            ) {
                // ── Drawer Header ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MDarkGray)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MBlueDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.displayName?.take(1)?.uppercase() ?: "?",
                            color = MTextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user?.displayName ?: "Mgeni",
                        color = MTextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user?.role?.replace("_", " ")?.uppercase() ?: "",
                        color = MBlueLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MStripesDivider(height = 2.dp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Drawer Items ───────────────────────────────────────────
                drawerItems.forEach { item ->
                    val isSelected = currentScreen == item.screen ||
                        (item.screen is Screen.ReportDamage && currentScreen is Screen.ReportDamage)
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navigateToRoot(item.screen)
                            closeDrawer()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MBlueDark.copy(alpha = 0.2f),
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = MBlueLight,
                            unselectedIconColor = MTextMuted,
                            selectedTextColor = MTextWhite,
                            unselectedTextColor = MTextMuted
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // ── Logout Button ──────────────────────────────────────────
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MRed,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Toka (Logout)", color = MRed) },
                    selected = false,
                    onClick = {
                        ApiClient.logout()
                        navigateToRoot(Screen.Landing)
                        closeDrawer()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = MRed.copy(alpha = 0.08f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isFullScreen) {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = screenTitle(currentScreen),
                                    color = MTextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            navigationIcon = {
                                if (isRootScreen) {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = MTextWhite
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { navigateBack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = MTextWhite
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MDarkGray,
                                titleContentColor = MTextWhite
                            )
                        )
                        MStripesDivider(height = 3.dp)
                    }
                }
            },
            bottomBar = {
                val showNav = !isFullScreen && ApiClient.accessToken != null
                if (showNav) {
                    NavigationBar(
                        containerColor = MDarkGray,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(68.dp)
                    ) {
                        NavigationBarItem(
                            selected = isRootScreen &&
                                currentScreen != Screen.Profile &&
                                currentScreen != Screen.Predictor,
                            onClick = { navigateToRoot(getRoleHomeRoute()) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MBlueLight,
                                selectedTextColor = MBlueLight,
                                indicatorColor = MBlueDark.copy(alpha = 0.2f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.ReportDamage,
                            onClick = { navigateToRoot(Screen.ReportDamage(null)) },
                            icon = { Icon(Icons.Default.ReportProblem, contentDescription = "Uharibifu") },
                            label = { Text("Ripoti", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MRed,
                                selectedTextColor = MRed,
                                indicatorColor = MRed.copy(alpha = 0.15f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Predictor,
                            onClick = { navigateToRoot(Screen.Predictor) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
                            label = { Text("AI", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF30D158),
                                selectedTextColor = Color(0xFF30D158),
                                indicatorColor = Color(0xFF30D158).copy(alpha = 0.15f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Profile,
                            onClick = { navigateToRoot(Screen.Profile) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MTextWhite,
                                selectedTextColor = MTextWhite,
                                indicatorColor = MBorderGray.copy(alpha = 0.5f),
                                unselectedIconColor = MTextMuted,
                                unselectedTextColor = MTextMuted
                            )
                        )
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
                    is Screen.Landing -> LandingScreen(
                        onNavigateToLogin = { navigateTo(Screen.Login) },
                        onNavigateToRegister = { navigateTo(Screen.Register) }
                    )
                    is Screen.Login -> LoginScreen(
                        onLoginSuccess = { navigateToRoot(getRoleHomeRoute()) },
                        onNavigateToRegister = { navigateTo(Screen.Register) }
                    )
                    is Screen.Register -> RegisterScreen(
                        onRegisterSuccess = { navigateToRoot(getRoleHomeRoute()) },
                        onNavigateToLogin = { navigateBack() }
                    )
                    is Screen.Dashboard -> DashboardScreen(
                        onNavigateToSourceDetails = { id -> navigateTo(Screen.WaterSourceDetails(id)) },
                        onNavigateToReportDamage = { id -> navigateTo(Screen.ReportDamage(id)) },
                        onNavigateToPredictor = { navigateTo(Screen.Predictor) }
                    )
                    is Screen.AdminPanel -> AdminScreen(
                        onNavigateToCitizen = { navigateTo(Screen.Dashboard) },
                        onNavigateToLeader = { navigateTo(Screen.VillageLeaderPanel) },
                        onNavigateToOfficer = { navigateTo(Screen.WaterOfficerPanel) },
                        onNavigateToDistrict = { navigateTo(Screen.DistrictOfficerPanel) },
                        onNavigateToPredictor = { navigateTo(Screen.Predictor) }
                    )
                    is Screen.DistrictOfficerPanel -> DistrictOfficerScreen(
                        onNavigateBack = { navigateBack() }
                    )
                    is Screen.VillageLeaderPanel -> VillageLeaderScreen(
                        onNavigateBack = { navigateBack() }
                    )
                    is Screen.WaterOfficerPanel -> WaterOfficerScreen(
                        onNavigateBack = { navigateBack() }
                    )
                    is Screen.Predictor -> PredictorScreen(
                        onNavigateBack = { navigateBack() }
                    )
                    is Screen.Profile -> ProfileScreen(
                        onNavigateBack = { navigateBack() },
                        onLogout = {
                            ApiClient.logout()
                            navigateToRoot(Screen.Landing)
                        }
                    )
                    is Screen.WaterSourceDetails -> WaterSourceDetailsScreen(
                        sourceId = currentScreen.sourceId,
                        onNavigateBack = { navigateBack() },
                        onNavigateToReportDamage = { id -> navigateTo(Screen.ReportDamage(id)) },
                        onNavigateToLogQuality = { id -> navigateTo(Screen.LogQuality(id)) }
                    )
                    is Screen.ReportDamage -> ReportDamageScreen(
                        initialSourceId = currentScreen.sourceId,
                        onNavigateBack = { navigateBack() },
                        onSuccess = { navigateBack() }
                    )
                    is Screen.LogQuality -> LogQualityScreen(
                        waterSourceId = currentScreen.sourceId,
                        onNavigateBack = { navigateBack() },
                        onSuccess = { navigateBack() }
                    )
                }
            }
        }
    }
}

// ── Screen title helper ──────────────────────────────────────────────────────
private fun screenTitle(screen: Screen): String = when (screen) {
    is Screen.Dashboard            -> "Vyanzo vya Maji"
    is Screen.AdminPanel           -> "Admin Dashboard"
    is Screen.VillageLeaderPanel   -> "Paneli ya Kiongozi"
    is Screen.WaterOfficerPanel    -> "Paneli ya Afisa Maji"
    is Screen.DistrictOfficerPanel -> "Paneli ya Wilaya"
    is Screen.Predictor            -> "AI Predictor"
    is Screen.Profile              -> "Wasifu Wangu"
    is Screen.ReportDamage         -> "Ripoti Uharibifu"
    is Screen.WaterSourceDetails   -> "Maelezo ya Chanzo"
    is Screen.LogQuality           -> "Ukaguzi wa Ubora"
    is Screen.Register             -> "Jiandikishe"
    else                           -> "WaterTrack"
}