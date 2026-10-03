package com.phonerepair.shop.ui.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonerepair.shop.R
import com.phonerepair.shop.ui.components.*
import com.phonerepair.shop.ui.theme.PhoneRepairTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons

@Composable
fun CustomersScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: CustomersViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var customers by remember { mutableStateOf<List<CustomerItem>>(sampleCustomers) }
    var isLoading by remember { mutableStateOf(false) }
    var showAddCustomer by remember { mutableStateOf(false) }

    LoadingOverlay(isLoading = isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("العملاء", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${customers.size} عميل • ${customers.sumOf { it.totalOrders }} طلب", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        CustomButton(
                            text = "عميل جديد",
                            onClick = { showAddCustomer = true },
                            icon = Icons.Default.PersonAdd,
                            modifier = Modifier.width(140.dp).height(44.dp)
                        )
                    }
                    
                    // Search Bar
                    CustomTextField(
                        label = "بحث",
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "ابحث بالاسم، الهاتف، الإيميل...",
                        leadingIcon = Icons.Default.Search,
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingIconClick = { searchQuery = "" }
                    )
                }
            }

            // Stats Row
            CustomerStatsRow(customers = customers)

            // Customers List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                items(customers.filter { customer ->
                    searchQuery.isBlank() || 
                    customer.name.contains(searchQuery, ignoreCase = true) ||
                    customer.phone.contains(searchQuery, ignoreCase = true) ||
                    customer.email?.contains(searchQuery, ignoreCase = true) == true
                }) { customer ->
                    CustomerCard(
                        customer = customer,
                        onClick = { onNavigateToDetail(customer.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerStatsRow(customers: List<CustomerItem>) {
    val totalSpent = customers.sumOf { it.totalSpent }
    val avgSpent = if (customers.isNotEmpty()) totalSpent / customers.size else 0.0
    val vipCount = customers.count { it.loyaltyPoints >= 500 }
    val newThisMonth = customers.count { it.isNewThisMonth }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox("إجمالي العملاء", "${customers.size}", Icons.Default.People, MaterialTheme.colorScheme.primary)
        StatBox("إجمالي المبيعات", formatCurrency(totalSpent), Icons.Default.AttachMoney, Color(0xFF2E7D32))
        StatBox("متوسط الإنفاق", formatCurrency(avgSpent), Icons.Default.TrendingUp, Color(0xFF9C27B0))
        StatBox("عملاء VIP", vipCount.toString(), Icons.Default.Star, Color(0xFFFFB300))
    }
}

@Composable
fun StatBox(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
                    Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
                }
                Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun CustomerCard(customer: CustomerItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircleAvatar(
                    modifier = Modifier.size(56.dp),
                    contentDescription = customer.name,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = customer.name.firstOrNull()?.toString() ?: "؟",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(customer.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (customer.loyaltyPoints >= 500) {
                        StatusChip(text = "VIP", color = Color(0xFFFFB300), icon = Icons.Default.Star)
                    }
                    if (customer.isNewThisMonth) {
                        StatusChip(text = "جديد", color = Color(0xFF4CAF50), icon = Icons.Default.NewLabel)
                    }
                    if (customer.isBlacklisted) {
                        StatusChip(text = "محظور", color = MaterialTheme.colorScheme.error, icon = Icons.Default.Block)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(icon = Icons.Default.Phone, label = "", value = customer.phone)
                    InfoItem(icon = Icons.Default.Email, label = "", value = customer.email ?: "—")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(icon = Icons.Default.ShoppingBag, label = "طلبات", value = "${customer.totalOrders}")
                    InfoItem(icon = Icons.Default.AttachMoney, label = "إجمالي الإنفاق", value = formatCurrency(customer.totalSpent), valueColor = Color(0xFF2E7D32))
                    InfoItem(icon = Icons.Default.Star, label = "نقاط", value = "${customer.loyaltyPoints}", valueColor = Color(0xFFFFB300))
                }
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        if (label.isNotBlank()) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = valueColor, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
    }
}

private fun formatCurrency(amount: Double): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("ar", "SA")).format(amount)
}

// Sample Data
val sampleCustomers = listOf(
    CustomerItem(
        id = "CUST-001",
        name = "أحمد محمد علي",
        phone = "0501234567",
        email = "ahmed@example.com",
        totalOrders = 12,
        totalSpent = 4500.0,
        loyaltyPoints = 750,
        isBlacklisted = false,
        isNewThisMonth = false,
        lastVisitAt = System.currentTimeMillis() - 86400000
    ),
    CustomerItem(
        id = "CUST-002",
        name = "سارة أحمد سالم",
        phone = "0559876543",
        email = "sara@example.com",
        totalOrders = 8,
        totalSpent = 3200.0,
        loyaltyPoints = 480,
        isBlacklisted = false,
        isNewThisMonth = false,
        lastVisitAt = System.currentTimeMillis() - 172800000
    ),
    CustomerItem(
        id = "CUST-003",
        name = "خالد عبدالله",
        phone = "0541112233",
        email = null,
        totalOrders = 3,
        totalSpent = 850.0,
        loyaltyPoints = 120,
        isBlacklisted = false,
        isNewThisMonth = true,
        lastVisitAt = System.currentTimeMillis() - 3600000
    ),
    CustomerItem(
        id = "CUST-004",
        name = "نورة خالد",
        phone = "0534445566",
        email = "noura@example.com",
        totalOrders = 15,
        totalSpent = 6800.0,
        loyaltyPoints = 1200,
        isBlacklisted = false,
        isNewThisMonth = false,
        lastVisitAt = System.currentTimeMillis() - 43200000
    ),
    CustomerItem(
        id = "CUST-005",
        name = "محمد سعيد",
        phone = "0567778899",
        email = "mohammed@example.com",
        totalOrders = 1,
        totalSpent = 180.0,
        loyaltyPoints = 15,
        isBlacklisted = false,
        isNewThisMonth = true,
        lastVisitAt = System.currentTimeMillis() - 1800000
    )
)

data class CustomerItem(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val totalOrders: Int,
    val totalSpent: Double,
    val loyaltyPoints: Int,
    val isBlacklisted: Boolean,
    val isNewThisMonth: Boolean,
    val lastVisitAt: Long
)

class CustomersViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading
}