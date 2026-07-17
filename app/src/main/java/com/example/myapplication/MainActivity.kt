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
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

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
    fun navigateReplace(screen: Screen) { if (backStack.size > 1) backStack.removeAt(backStack.size - 1); backStack.add(screen) }
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

    // ── Screens where ALL chrome (top + bottom bars) is hidden ─────────────
    // Only Login / Register are truly full-screen (they are form flows)
    val isFullScreen = currentScreen is Screen.Login ||
                       currentScreen is Screen.Register

    // ── Landing is public — hide top bar but SHOW bottom nav ─────────────
    val isLandingScreen = currentScreen is Screen.Landing

    // ── Screens where we show a hamburger menu icon ────────────────────────
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
        gesturesEnabled = !isFullScreen && !isLandingScreen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 0.dp
            ) {
                // ── Drawer Header ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.displayName?.take(1)?.uppercase() ?: "?",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user?.displayName ?: "Mgeni",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user?.role?.replace("_", " ")?.uppercase() ?: "",
                        color = MaterialTheme.colorScheme.secondary,
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
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Toka (Logout)", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        ApiClient.logout()
                        navigateToRoot(Screen.Landing)
                        closeDrawer()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // Landing has its own built-in header — skip the top app bar for it
                if (!isFullScreen && !isLandingScreen) {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = screenTitle(currentScreen),
                                    color = MaterialTheme.colorScheme.onSurface,
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
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { navigateBack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        MStripesDivider(height = 3.dp)
                    }
                }
            },
            bottomBar = {
                // Show bottom nav on Landing (guest nav) and all authenticated screens
                if (!isFullScreen) {
                    NavigationBar(
                        containerColor = BlueAbyss,
                        tonalElevation = 8.dp
                    ) {
                        if (isLandingScreen) {
                            // ── Guest navigation (public Landing page) ────────
                            NavigationBarItem(
                                selected = true,
                                onClick = { /* already here */ },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Nyumbani") },
                                label = { Text("Nyumbani", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navigateTo(Screen.Login) },
                                icon = { Icon(Icons.Default.Login, contentDescription = "Ingia") },
                                label = { Text("Ingia", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navigateTo(Screen.Register) },
                                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Jiandikishe") },
                                label = { Text("Jiandikishe", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                        } else {
                            // ── Authenticated navigation ───────────────────────
                            NavigationBarItem(
                                selected = isRootScreen &&
                                    currentScreen != Screen.Profile &&
                                    currentScreen != Screen.Predictor,
                                onClick = { navigateToRoot(getRoleHomeRoute()) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen is Screen.ReportDamage,
                                onClick = { navigateToRoot(Screen.ReportDamage(null)) },
                                icon = { Icon(Icons.Default.ReportProblem, contentDescription = "Uharibifu") },
                                label = { Text("Ripoti", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen is Screen.Predictor,
                                onClick = { navigateToRoot(Screen.Predictor) },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI") },
                                label = { Text("AI", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen is Screen.Profile,
                                onClick = { navigateToRoot(Screen.Profile) },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BlueAbyss,
                                    selectedTextColor = WhitePure,
                                    indicatorColor = WhitePure,
                                    unselectedIconColor = WhitePure.copy(alpha = 0.6f),
                                    unselectedTextColor = WhitePure.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    is Screen.Landing -> LandingScreen(
                        onNavigateToLogin = { navigateTo(Screen.Login) },
                        onNavigateToRegister = { navigateReplace(Screen.Register) }
                    )
                    is Screen.Login -> LoginScreen(
                        onLoginSuccess = { navigateToRoot(getRoleHomeRoute()) },
                        onNavigateToRegister = { navigateReplace(Screen.Register) }
                    )
                    is Screen.Register -> RegisterScreen(
                        onRegisterSuccess = { navigateToRoot(getRoleHomeRoute()) },
                        onNavigateToLogin = { navigateReplace(Screen.Login) }
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