package com.phonerepair.shop.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.navigation.compose.composable
import androidx.compose.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonerepair.shop.R
import com.phonerepair.shop.ui.auth.AuthScreen
import com.phonerepair.shop.ui.dashboard.DashboardScreen
import com.phonerepair.shop.ui.inventory.InventoryScreen
import com.phonerepair.shop.ui.invoices.InvoicesScreen
import com.phonerepair.shop.ui.repairs.RepairsScreen
import com.phonerepair.shop.ui.reports.ReportsScreen
import com.phonerepair.shop.ui.settings.SettingsScreen
import com.phonerepair.shop.ui.theme.PhoneRepairTheme

@Composable
fun AppNavHost(
    isLoggedIn: Boolean,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentRoute by remember { mutableStateOf("dashboard") }
    
    if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { /* handled by parent */ })
        return
    }

    androidx.navigation.compose.NavHost(navController, "dashboard") {
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
            route = "repair/$repairId",
            arguments = listOf(androidx.navigation.navArgument("repairId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val repairId = backStackEntry.getString() ?: ""
            RepairDetailScreen(repairId = repairId)
        }
        
        composable("inventory") {
            InventoryScreen(onNavigateToDetail = { id -> navController.navigate("part/$id") })
        }
        
        composable(
            route = "part/$partId",
            arguments = listOf(androidx.navigation.navArgument("partId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val partId = backStackEntry.getString() ?: ""
            PartDetailScreen(partId = partId)
        }
        
        composable("customers") {
            CustomersScreen(onNavigateToDetail = { id -> navController.navigate("customer/$id") })
        }
        
        composable(
            route = "customer/$customerId",
            arguments = listOf(androidx.navigation.navArgument("customerId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.getString() ?: ""
            CustomerDetailScreen(customerId = customerId)
        }
        
        composable("invoices") {
            InvoicesScreen(onNavigateToDetail = { id -> navController.navigate("invoice/$id") })
        }
        
        composable(
            route = "invoice/$invoiceId",
            arguments = listOf(androidx.navigation.navArgument("invoiceId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.getString() ?: ""
            InvoiceDetailScreen(invoiceId = invoiceId)
        }
        
        composable("reports") {
            ReportsScreen()
        }
        
        composable("settings") {
            SettingsScreen(onLogout = onLogout)
        }
    }
}

@Composable
fun MainBottomNavigation(
    navController: androidx.navigation.NavController,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("dashboard", "الرئيسية", Icons.Default.Home, Icons.Default.HomeOutlined),
        NavigationItem("repairs", "الصيانة", Icons.Default.Build, Icons.Default.BuildOutlined),
        NavigationItem("inventory", "المخزون", Icons.Default.Inventory, Icons.Default.InventoryOutlined),
        NavigationItem("customers", "العملاء", Icons.Default.People, Icons.Default.PeopleOutlined),
        NavigationItem("invoices", "الفواتير", Icons.Default.Receipt, Icons.Default.ReceiptOutlined),
        NavigationItem("reports", "التقارير", Icons.Default.BarChart, Icons.Default.BarChartOutlined),
        NavigationItem("settings", "الإعدادات", Icons.Default.Settings, Icons.Default.SettingsOutlined)
    )
    
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
    ) {
        items.forEach { item ->
            val selected = navController.currentBackStackEntryAsState().value?.destination?.route?.startsWith(item.route) == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconFilled else item.iconOutlined,
                        contentDescription = item.label,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = selected,
                onClick = { onNavigate(item.route) },
                alwaysShowLabel = true
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    val iconOutlined: androidx.compose.ui.graphics.vector.ImageVector
)

// Placeholder screens - to be implemented
@Composable
fun RepairDetailScreen(repairId: String) {
    androidx.compose.material3.Text(text = "تفاصيل طلب الصيانة: $repairId", fontSize = 20.sp, modifier = androidx.compose.foundation.layout.Modifier.padding(24.dp))
}

@Composable
fun PartDetailScreen(partId: String) {
    androidx.compose.material3.Text(text = "تفاصيل القطعة: $partId", fontSize = 20.sp, modifier = androidx.compose.foundation.layout.Modifier.padding(24.dp))
}

@Composable
fun CustomerDetailScreen(customerId: String) {
    androidx.compose.material3.Text(text = "تفاصيل العميل: $customerId", fontSize = 20.sp, modifier = androidx.compose.foundation.layout.Modifier.padding(24.dp))
}

@Composable
fun InvoiceDetailScreen(invoiceId: String) {
    androidx.compose.material3.Text(text = "تفاصيل الفاتورة: $invoiceId", fontSize = 20.sp, modifier = androidx.compose.foundation.layout.Modifier.padding(24.dp))
}