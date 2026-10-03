package com.phonerepair.shop.ui.repairs

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
import androidx.compose.material3.Chip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
fun RepairsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: RepairsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var orders by remember { mutableStateOf<List<RepairOrderItem>>(sampleOrders) }
    var isLoading by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showAddOrder by remember { mutableStateOf(false) }

    LoadingOverlay(isLoading = isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header with Search
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
                            Text("طلبات الصيانة", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${orders.size} طلب", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomIconButton(
                                onClick = { showFilters = !showFilters },
                                icon = if (showFilters) Icons.Default.FilterList else Icons.Outlined.FilterList,
                                contentDescription = "تصفية"
                            )
                            CustomButton(
                                text = "طلب جديد",
                                onClick = { showAddOrder = true },
                                icon = Icons.Default.Add,
                                modifier = Modifier.width(140.dp).height(44.dp)
                            )
                        }
                    }
                    
                    // Search Bar
                    CustomTextField(
                        label = "بحث",
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "ابحث بالاسم، الجهاز، الرقم...",
                        leadingIcon = Icons.Default.Search,
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingIconClick = { searchQuery = "" }
                    )
                }
            }

            // Filter Chips
            if (showFilters) {
                FilterChipsSection(
                    selectedStatus = selectedStatus,
                    onStatusChange = { selectedStatus = it },
                    selectedPriority = selectedPriority,
                    onPriorityChange = { selectedPriority = it }
                )
            }

            // Orders List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                items(orders.filter { order ->
                    (searchQuery.isBlank() || 
                     order.customerName.contains(searchQuery, ignoreCase = true) ||
                     order.deviceModel.contains(searchQuery, ignoreCase = true) ||
                     order.id.contains(searchQuery, ignoreCase = true)) &&
                    (selectedStatus == null || order.status == selectedStatus) &&
                    (selectedPriority == null || order.priority == selectedPriority)
                }) { order ->
                    RepairOrderCard(
                        order = order,
                        onClick = { onNavigateToDetail(order.id) },
                        onStatusChange = { newStatus ->
                            orders = orders.map { if (it.id == order.id) it.copy(status = newStatus) else it }
                        },
                        onPriorityChange = { newPriority ->
                            orders = orders.map { if (it.id == order.id) it.copy(priority = newPriority) else it }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipsSection(
    selectedStatus: String?,
    onStatusChange: (String?) -> Unit,
    selectedPriority: String?,
    onPriorityChange: (String?) -> Unit
) {
    val statuses = listOf(
        null to "الكل",
        "RECEIVED" to "تم الاستلام",
        "DIAGNOSING" to "قيد التشخيص",
        "WAITING_PARTS" to "بانتظار القطع",
        "IN_REPAIR" to "قيد الإصلاح",
        "READY_FOR_PICKUP" to "جاهز للاستلام",
        "DELIVERED" to "تم التسليم",
        "CANCELLED" to "ملغي"
    )
    
    val priorities = listOf(
        null to "الكل",
        "URGENT" to "عاجل",
        "HIGH" to "عالي",
        "NORMAL" to "عادي",
        "LOW" to "منخفض"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("الحالة:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f))
        }
        
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statuses.forEach { (value, label) ->
                val isSelected = selectedStatus == value
                Chip(
                    onClick = { onStatusChange(value) },
                    colors = androidx.compose.material3.ChipDefaults.chipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("الأولوية:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f))
        }
        
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priorities.forEach { (value, label) ->
                val isSelected = selectedPriority == value
                Chip(
                    onClick = { onPriorityChange(value) },
                    colors = androidx.compose.material3.ChipDefaults.chipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun RepairOrderCard(
    order: RepairOrderItem,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(order.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    PriorityChip(priority = order.priority)
                }
                RepairStatusChip(status = order.status)
            }
            
            // Customer & Device Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(
                        icon = Icons.Default.Person,
                        label = "العميل",
                        value = order.customerName
                    )
                    InfoItem(
                        icon = Icons.Default.PhoneAndroid,
                        label = "الجهاز",
                        value = order.deviceModel
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(
                        icon = Icons.Default.Call,
                        label = "الهاتف",
                        value = order.customerPhone
                    )
                    InfoItem(
                        icon = Icons.Default.Schedule,
                        label = "الاستلام",
                        value = formatDate(order.createdAt)
                    )
                }
            }
            
            // Issue & Progress
            if (order.reportedIssue.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row {
                        Text("العطل المبلغ عنه:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(order.reportedIssue, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                }
            }
            
            // Technician & Cost
            if (order.technicianName != null || order.estimatedCost > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (order.technicianName != null) {
                        InfoItem(
                            icon = Icons.Default.Engineering,
                            label = "الفني",
                            value = order.technicianName!!
                        )
                    }
                    if (order.estimatedCost > 0) {
                        InfoItem(
                            icon = Icons.Default.AttachMoney,
                            label = "التكلفة المتوقعة",
                            value = formatCurrency(order.estimatedCost)
                        )
                    }
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomButton(
                    text = "تحديث الحالة",
                    onClick = { /* show status dialog */ },
                    icon = Icons.Default.SwapVert,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                CustomButton(
                    text = "عرض التفاصيل",
                    onClick = onClick,
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
}

private fun formatCurrency(amount: Double): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("ar", "SA")).format(amount)
}

// Sample Data
val sampleOrders = listOf(
    RepairOrderItem(
        id = "RO-2024-001",
        customerName = "أحمد محمد",
        customerPhone = "0501234567",
        deviceModel = "iPhone 15 Pro Max",
        reportedIssue = "الشاشة مكسورة ولا تعمل",
        status = "IN_REPAIR",
        priority = "HIGH",
        technicianName = "محمد علي",
        estimatedCost = 850.0,
        createdAt = System.currentTimeMillis() - 86400000
    ),
    RepairOrderItem(
        id = "RO-2024-002",
        customerName = "سارة أحمد",
        customerPhone = "0559876543",
        deviceModel = "Samsung Galaxy S24",
        reportedIssue = "البطارية تفرغ بسرعة",
        status = "READY_FOR_PICKUP",
        priority = "NORMAL",
        technicianName = "فاطمة سالم",
        estimatedCost = 320.0,
        createdAt = System.currentTimeMillis() - 172800000
    ),
    RepairOrderItem(
        id = "RO-2024-003",
        customerName = "خالد عبدالله",
        customerPhone = "0541112233",
        deviceModel = "iPhone 13",
        reportedIssue = "لا يشحن، منفذ الشحن تالف",
        status = "WAITING_PARTS",
        priority = "URGENT",
        technicianName = "أحمد حسن",
        estimatedCost = 180.0,
        createdAt = System.currentTimeMillis() - 259200000
    ),
    RepairOrderItem(
        id = "RO-2024-004",
        customerName = "نورة خالد",
        customerPhone = "0534445566",
        deviceModel = "Xiaomi 14 Ultra",
        reportedIssue = "الكاميرا الخلفية لا تفتح",
        status = "DIAGNOSING",
        priority = "NORMAL",
        technicianName = nil,
        estimatedCost = 0.0,
        createdAt = System.currentTimeMillis() - 3600000
    ),
    RepairOrderItem(
        id = "RO-2024-005",
        customerName = "محمد سعيد",
        customerPhone = "0567778899",
        deviceModel = "iPhone 14 Pro",
        reportedIssue = " Face ID لا يعمل بعد سقوط",
        status = "RECEIVED",
        priority = "HIGH",
        technicianName = nil,
        estimatedCost = 0.0,
        createdAt = System.currentTimeMillis() - 1800000
    )
)

data class RepairOrderItem(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val deviceModel: String,
    val reportedIssue: String,
    val status: String,
    val priority: String,
    val technicianName: String?,
    val estimatedCost: Double,
    val createdAt: Long
)

class RepairsViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading
}