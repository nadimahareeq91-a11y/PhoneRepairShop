package com.phonerepair.shop.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phonerepair.shop.ui.auth.AuthScreen
import com.phonerepair.shop.ui.customers.CustomersScreen
import com.phonerepair.shop.ui.dashboard.DashboardScreen
import com.phonerepair.shop.ui.inventory.InventoryScreen
import com.phonerepair.shop.ui.invoices.InvoicesScreen
import com.phonerepair.shop.ui.repairs.RepairsScreen
import com.phonerepair.shop.ui.reports.ReportsScreen
import com.phonerepair.shop.ui.settings.SettingsScreen

@Composable
fun MainScreen() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { isLoggedIn = true })
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            MainBottomNavigation(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AppNavHost(navController = navController)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToRepairs = { navController.navigate("repairs") },
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToCustomers = { navController.navigate("customers") },
                onNavigateToInvoices = { navController.navigate("invoices") },
                onNavigateToReports = { navController.navigate("reports") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("repairs") {
            RepairsScreen(onNavigateToDetail = { id -> navController.navigate("repair/$id") })
        }

        composable(
            route = "repair/{repairId}",
            arguments = listOf(navArgument("repairId") { type = NavType.StringType })
        ) { entry ->
            RepairDetailScreen(repairId = entry.arguments?.getString("repairId").orEmpty())
        }

        composable("inventory") {
            InventoryScreen(onNavigateToDetail = { id -> navController.navigate("part/$id") })
        }

        composable(
            route = "part/{partId}",
            arguments = listOf(navArgument("partId") { type = NavType.StringType })
        ) { entry ->
            PartDetailScreen(partId = entry.arguments?.getString("partId").orEmpty())
        }

        composable("customers") {
            CustomersScreen(onNavigateToDetail = { id -> navController.navigate("customer/$id") })
        }

        composable(
            route = "customer/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { entry ->
            CustomerDetailScreen(customerId = entry.arguments?.getString("customerId").orEmpty())
        }

        composable("invoices") {
            InvoicesScreen(onNavigateToDetail = { id -> navController.navigate("invoice/$id") })
        }

        composable(
            route = "invoice/{invoiceId}",
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
        ) { entry ->
            InvoiceDetailScreen(invoiceId = entry.arguments?.getString("invoiceId").orEmpty())
        }

        composable("reports") {
            ReportsScreen()
        }

        composable("settings") {
            SettingsScreen(onLogout = { })
        }
    }
}

@Composable
fun MainBottomNavigation(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        NavigationItem("dashboard", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("repairs", "الصيانة", Icons.Filled.Build, Icons.Outlined.Build),
        NavigationItem("inventory", "المخزون", Icons.Filled.Inventory, Icons.Outlined.Inventory),
        NavigationItem("customers", "العملاء", Icons.Filled.People, Icons.Outlined.People),
        NavigationItem("invoices", "الفواتير", Icons.Filled.Receipt, Icons.Outlined.Receipt),
        NavigationItem("reports", "التقارير", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        NavigationItem("settings", "الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        items.forEach { item ->
            val selected = currentRoute?.substringBefore("/") == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconFilled else item.iconOutlined,
                        contentDescription = item.label,
                        modifier = Modifier.padding(6.dp)
                    )
                },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo("dashboard") { inclusive = item.route == "dashboard" }
                            launchSingleTop = true
                        }
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

@Composable
fun RepairDetailScreen(repairId: String) {
    DetailPlaceholder(title = "تفاصيل طلب الصيانة", value = repairId)
}

@Composable
fun PartDetailScreen(partId: String) {
    DetailPlaceholder(title = "تفاصيل القطعة", value = partId)
}

@Composable
fun CustomerDetailScreen(customerId: String) {
    DetailPlaceholder(title = "تفاصيل العميل", value = customerId)
}

@Composable
fun InvoiceDetailScreen(invoiceId: String) {
    DetailPlaceholder(title = "تفاصيل الفاتورة", value = invoiceId)
}

@Composable
private fun DetailPlaceholder(title: String, value: String) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text(text = title, fontSize = 20.sp)
        Text(text = value, fontSize = 16.sp)
    }
}