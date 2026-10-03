package com.phonerepair.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonerepair.shop.ui.auth.AuthScreen
import com.phonerepair.shop.ui.navigation.AppNavHost
import com.phonerepair.shop.ui.navigation.MainBottomNavigation
import com.phonerepair.shop.ui.theme.PhoneRepairTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModel()
    private val navigationViewModel: NavigationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneRepairTheme {
                MaterialTheme {
                    Surface(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
    
    @Composable
    fun MainScreen() {
        val isLoggedIn by remember { mutableStateOf(false) }
        
        if (!isLoggedIn) {
            AuthScreen(onLoginSuccess = { isLoggedIn = true })
        } else {
            val navController = rememberNavController()
            val currentRoute by remember { mutableStateOf("dashboard") }
            
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
                    SettingsScreen(onLogout = { isLoggedIn = false })
                }
            }
            
            // Bottom Navigation
            Box(modifier = Modifier.fillMaxSize()) {
                // Content
            }
        }
    }
}

// Simple ViewModels for navigation
class AuthViewModel : androidx.lifecycle.ViewModel() {
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        kotlinx.coroutines.delay(1000)
        callback(true, null)
    }
}

class NavigationViewModel : androidx.lifecycle.ViewModel()

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