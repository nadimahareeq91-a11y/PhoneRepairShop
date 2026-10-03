package com.phonerepair.shop.ui.dashboard

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
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyHorizontalGrid
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.gridItems
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
import com.phonerepair.shop.ui.navigation.NavigationItem
import com.phonerepair.shop.ui.theme.PhoneRepairTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons

@Composable
fun DashboardScreen(
    onNavigateToRepairs: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var stats by remember { mutableStateOf(DashboardStats()) }
    var recentOrders by remember { mutableStateOf<List<RepairOrderSummary>>(emptyList()) }
    var lowStockParts by remember { mutableStateOf<List<PartSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load data
    androidx.compose.runtime.LaunchedEffect(Unit) {
        loadData()
    }

    fun loadData() {
        // Simulate loading
        kotlinx.coroutines.delay(500)
        stats = DashboardStats(
            todayOrders = 12,
            inProgress = 5,
            readyForPickup = 3,
            completedToday = 7,
            totalRevenue = 15750.0,
            pendingPayments = 3200.0,
            lowStockCount = 8,
            totalCustomers = 245
        )
        recentOrders = (1..5).map { i ->
            RepairOrderSummary(
                id = "RO-$i",
                customerName = "عميل $i",
                deviceModel = "iPhone 1${i} Pro",
                status = listOf("RECEIVED", "IN_REPAIR", "READY_FOR_PICKUP", "DIAGNOSING", "WAITING_PARTS")[i - 1],
                priority = listOf("HIGH", "NORMAL", "URGENT", "LOW", "NORMAL")[i - 1],
                createdAt = java.util.Date().time - i * 3600000L
            )
        }
        lowStockParts = (1..8).map { i ->
            PartSummary(
                id = "PART-$i",
                name = "قطعة $i",
                currentStock = i,
                minStock = 10,
                category = "الشاشات"
            )
        }
        isLoading = false
    }

    LoadingOverlay(isLoading = isLoading, message = "جاري تحميل لوحة التحكم...") {
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "لوحة التحكم",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مرحباً بك في محل صيانة الهواتف",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CustomIconButton(
                        onClick = onNavigateToSettings,
                        icon = Icons.Default.Settings,
                        contentDescription = "الإعدادات"
                    )
                }

                // Quick Stats Grid
                SectionHeader(title = "نظرة سريعة", subtitle = "إحصائيات اليوم")
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    items(quickStats) { stat ->
                        StatCard(stat = stat)
                    }
                }

                // Quick Actions
                SectionHeader(title = "إجراءات سريعة", actionText = "الكل", onActionClick = onNavigateToRepairs)
                
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    items(quickActions) { action ->
                        ActionCard(action = action)
                    }
                }

                // Recent Orders
                SectionHeader(
                    title = "أحدث طلبات الصيانة",
                    subtitle = "${recentOrders.size} طلب حديث",
                    actionText = "الكل",
                    onActionClick = onNavigateToRepairs
                )
                
                if (recentOrders.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Inventory,
                        title = "لا توجد طلبات",
                        message = "ابدأ بإضافة أول طلب صيانة"
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentOrders.forEach { order ->
                            RepairOrderCard(order = order, onClick = { /* navigate to detail */ })
                        }
                    }
                }

                // Low Stock Alert
                if (lowStockParts.isNotEmpty()) {
                    SectionHeader(
                        title = "تنبيه: مخزون منخفض",
                        subtitle = "${lowStockParts.size} قطعة تحتاج إعادة توريد",
                        actionText = "عرض الكل",
                        onActionClick = onNavigateToInventory
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lowStockParts.take(3).forEach { part ->
                            LowStockCard(part = part)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(stat: StatItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = stat.color.copy(alpha = 0.1f)
        )
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stat.value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = stat.color
                    )
                    Text(
                        text = stat.label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = stat.color.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCard(action: QuickAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = action.onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = action.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RepairOrderCard(order: RepairOrderSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.id,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PriorityChip(priority = order.priority)
                }
                Text(
                    text = "${order.customerName} • ${order.deviceModel}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )
                Text(
                    text = formatTimeAgo(order.createdAt),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RepairStatusChip(status = order.status)
        }
    }
}

@Composable
fun LowStockCard(part: PartSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = part.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "المخزون الحالي: ${part.currentStock} / الحد الأدنى: ${part.minStock}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusChip(
                text = "منخفض",
                color = MaterialTheme.colorScheme.error,
                icon = Icons.Default.Warning
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        minutes < 1 -> "الآن"
        minutes < 60 -> "منذ $minutes دقيقة"
        hours < 24 -> "منذ $hours ساعة"
        days < 7 -> "منذ $days يوم"
        else -> java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }
}

// Data classes
data class DashboardStats(
    val todayOrders: Int = 0,
    val inProgress: Int = 0,
    val readyForPickup: Int = 0,
    val completedToday: Int = 0,
    val totalRevenue: Double = 0.0,
    val pendingPayments: Double = 0.0,
    val lowStockCount: Int = 0,
    val totalCustomers: Int = 0
)

data class RepairOrderSummary(
    val id: String,
    val customerName: String,
    val deviceModel: String,
    val status: String,
    val priority: String,
    val createdAt: Long
)

data class PartSummary(
    val id: String,
    val name: String,
    val currentStock: Int,
    val minStock: Int,
    val category: String
)

data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

val quickStats = listOf(
    StatItem("طلبات اليوم", "12", Icons.Default.Assignment, MaterialTheme.colorScheme.primary),
    StatItem("قيد الإصلاح", "5", Icons.Default.Build, Color(0xFF3F51B5)),
    StatItem("جاهز للاستلام", "3", Icons.Default.CheckCircle, Color(0xFF4CAF50)),
    StatItem("إيرادات اليوم", "15,750 ر.س", Icons.Default.AttachMoney, Color(0xFF2E7D32)),
    StatItem("مدفوعات معلقة", "3,200 ر.س", Icons.Default.Pending, Color(0xFFFF9800)),
    StatItem("قطع منخفضة", "8", Icons.Default.Warning, MaterialTheme.colorScheme.error),
    StatItem("إجمالي العملاء", "245", Icons.Default.People, Color(0xFF9C27B0)),
    StatItem("مكتمل اليوم", "7", Icons.Default.TaskAlt, Color(0xFF009688))
)

val quickActions = listOf(
    QuickAction("طلب صيانة جديد", Icons.Default.AddCircle, onNavigateToRepairs),
    QuickAction("إضافة قطعة", Icons.Default.AddBox, onNavigateToInventory),
    QuickAction("عميل جديد", Icons.Default.PersonAdd, onNavigateToCustomers),
    QuickAction("فاتورة جديدة", Icons.Default.ReceiptLong, onNavigateToInvoices),
    QuickAction("مسح باركود", Icons.Default.QrCodeScanner, { /* barcode scanner */ }),
    QuickAction("طباعة فاتورة", Icons.Default.Print, { /* print */ })
)

class DashboardViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading from repositories
}