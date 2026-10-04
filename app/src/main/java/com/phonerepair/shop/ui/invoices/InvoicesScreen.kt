package com.phonerepair.shop.ui.invoices

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FilterList
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonerepair.shop.R
import com.phonerepair.shop.ui.components.*
import com.phonerepair.shop.ui.theme.PhoneRepairTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun InvoicesScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: InvoicesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var invoices by remember { mutableStateOf<List<InvoiceItem>>(sampleInvoices) }
    var isLoading by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showAddInvoice by remember { mutableStateOf(false) }

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
                            Text("الفواتير", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${invoices.size} فاتورة • ${formatCurrency(invoices.sumOf { it.totalAmount })}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomIconButton(
                                onClick = { showFilters = !showFilters },
                                icon = if (showFilters) Icons.Default.FilterList else Icons.Outlined.FilterList,
                                contentDescription = "تصفية"
                            )
                            CustomButton(
                                text = "فاتورة جديدة",
                                onClick = { showAddInvoice = true },
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
                        placeholder = "ابحث برقم الفاتورة، العميل...",
                        leadingIcon = Icons.Default.Search,
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingIconClick = { searchQuery = "" }
                    )
                }
            }

            // Stats Row
            InvoiceStatsRow(invoices = invoices)

            // Filter Chips
            if (showFilters) {
                PaymentStatusFilterChips(
                    selectedStatus = selectedStatus,
                    onStatusChange = { selectedStatus = it }
                )
            }

            // Invoices List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                items(invoices.filter { invoice ->
                    (searchQuery.isBlank() || 
                     invoice.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                     invoice.customerName.contains(searchQuery, ignoreCase = true)) &&
                    (selectedStatus == null || invoice.paymentStatus == selectedStatus)
                }) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onClick = { onNavigateToDetail(invoice.id) },
                        onPrint = { /* print invoice */ },
                        onAddPayment = { /* add payment */ }
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceStatsRow(invoices: List<InvoiceItem>) {
    val totalRevenue = invoices.sumOf { it.totalAmount }
    val paidAmount = invoices.sumOf { it.paidAmount }
    val pendingAmount = invoices.sumOf { it.totalAmount - it.paidAmount }
    val overdueCount = invoices.count { it.isOverdue }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox("إجمالي الفواتير", formatCurrency(totalRevenue), Icons.Default.Receipt, MaterialTheme.colorScheme.primary)
        StatBox("المدفوع", formatCurrency(paidAmount), Icons.Default.CheckCircle, Color(0xFF4CAF50))
        StatBox("معلق", formatCurrency(pendingAmount), Icons.Default.Pending, Color(0xFFFF9800))
        StatBox("متأخرة", overdueCount.toString(), Icons.Default.Warning, MaterialTheme.colorScheme.error)
    }
}

@Composable
fun RowScope.StatBox(title: String, value: String, icon: ImageVector, color: Color) {
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
fun PaymentStatusFilterChips(
    selectedStatus: String?,
    onStatusChange: (String?) -> Unit
) {
    val statuses = listOf(
        null to "الكل",
        "PENDING" to "معلق",
        "PARTIAL" to "مدفوع جزئياً",
        "PAID" to "مدفوع بالكامل",
        "OVERDUE" to "متأخر",
        "REFUNDED" to "مسترد",
        "CANCELLED" to "ملغي"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statuses.forEach { (value, label) ->
                val isSelected = selectedStatus == value
                androidx.compose.material3.FilterChip(
                    selected = isSelected,
                    onClick = { onStatusChange(value) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: InvoiceItem,
    onClick: () -> Unit,
    onPrint: () -> Unit,
    onAddPayment: () -> Unit
) {
    val paymentStatus = invoice.paymentStatusObj
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = when (invoice.paymentStatus) {
                "OVERDUE" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
                "PAID" -> Color(0xFF4CAF50).copy(alpha = 0.05f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
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
                    Text(invoice.invoiceNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(formatDate(invoice.createdAt), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip(
                    text = paymentStatus.displayName,
                    color = Color(paymentStatus.color),
                    icon = paymentStatus.icon
                )
            }
            
            // Customer Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(icon = Icons.Default.Person, label = "العميل", value = invoice.customerName)
                InfoItem(icon = Icons.Default.Phone, label = "الهاتف", value = invoice.customerPhone)
                if (invoice.repairOrderId != null) {
                    InfoItem(icon = Icons.Default.Build, label = "طلب صيانة", value = invoice.repairOrderId!!)
                }
            }
            
            // Amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    icon = Icons.Default.AttachMoney,
                    label = "الإجمالي",
                    value = formatCurrency(invoice.totalAmount),
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
                InfoItem(
                    icon = Icons.Default.Paid,
                    label = "المدفوع",
                    value = formatCurrency(invoice.paidAmount),
                    valueColor = Color(0xFF4CAF50)
                )
                InfoItem(
                    icon = Icons.Default.MoneyOff,
                    label = "المتبقي",
                    value = formatCurrency(invoice.totalAmount - invoice.paidAmount),
                    valueColor = if (invoice.paymentStatus == "PAID") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
            
            // Payment Method
            if (invoice.paymentMethod != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        icon = paymentMethodIcon(invoice.paymentMethod!!),
                        label = "طريقة الدفع",
                        value = paymentMethodName(invoice.paymentMethod!!)
                    )
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (invoice.paymentStatus != "PAID") {
                    CustomButton(
                        text = "إضافة دفعة",
                        onClick = onAddPayment,
                        icon = Icons.Default.AddCircle,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f),
                            contentColor = Color(0xFF2E7D32)
                        )
                    )
                }
                CustomButton(
                    text = "طباعة",
                    onClick = onPrint,
                    icon = Icons.Default.Print,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                CustomButton(
                    text = "تفاصيل",
                    onClick = onClick,
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RowScope.InfoItem(icon: ImageVector, label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = valueColor, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
}

private fun formatCurrency(amount: Double): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("ar", "SA")).format(amount)
}

private fun paymentMethodIcon(method: String): ImageVector {
    return when (method) {
        "CASH" -> Icons.Default.Payments
        "CARD" -> Icons.Default.CreditCard
        "BANK_TRANSFER" -> Icons.Default.AccountBalance
        "WALLET" -> Icons.Default.AccountBalanceWallet
        "INSTALLMENTS" -> Icons.Default.CalendarMonth
        else -> Icons.Default.AttachMoney
    }
}

private fun paymentMethodName(method: String): String {
    return when (method) {
        "CASH" -> "نقدي"
        "CARD" -> "بطاقة"
        "BANK_TRANSFER" -> "تحويل بنكي"
        "WALLET" -> "محفظة إلكترونية"
        "INSTALLMENTS" -> "أقساط"
        else -> "أخرى"
    }
}

sealed class PaymentStatusObj(val displayName: String, val color: Color, val icon: ImageVector) {
    object PENDING : PaymentStatusObj("معلق", Color(0xFFFF9800), Icons.Default.Pending)
    object PARTIAL : PaymentStatusObj("مدفوع جزئياً", Color(0xFF2196F3), Icons.Default.RemoveCircle)
    object PAID : PaymentStatusObj("مدفوع بالكامل", Color(0xFF4CAF50), Icons.Default.CheckCircle)
    object OVERDUE : PaymentStatusObj("متأخر", Icons.Default.Error, MaterialTheme.colorScheme.error)
    object REFUNDED : PaymentStatusObj("مسترد", Color(0xFF9C27B0), Icons.Default.MoneyOff)
    object CANCELLED : PaymentStatusObj("ملغي", Color(0xFF607D8B), Icons.Default.Cancel)
}

val sampleInvoices = listOf(
    InvoiceItem(
        id = "INV-001",
        invoiceNumber = "INV-20240115-001",
        customerName = "أحمد محمد علي",
        customerPhone = "0501234567",
        repairOrderId = "RO-2024-001",
        totalAmount = 1200.0,
        paidAmount = 1200.0,
        paymentStatus = "PAID",
        paymentMethod = "CASH",
        createdAt = System.currentTimeMillis() - 86400000,
        dueDate = System.currentTimeMillis() + 86400000,
        items = listOf(
            InvoiceItemDetail("شاشة iPhone 15 Pro", 1, 750.0),
            InvoiceItemDetail("بطارية iPhone 15", 1, 180.0),
            InvoiceItemDetail("عمالة تركيب", 1, 270.0)
        )
    ),
    InvoiceItem(
        id = "INV-002",
        invoiceNumber = "INV-20240115-002",
        customerName = "سارة أحمد سالم",
        customerPhone = "0559876543",
        repairOrderId = "RO-2024-002",
        totalAmount = 850.0,
        paidAmount = 400.0,
        paymentStatus = "PARTIAL",
        paymentMethod = "CARD",
        createdAt = System.currentTimeMillis() - 172800000,
        dueDate = System.currentTimeMillis() + 172800000,
        items = listOf(
            InvoiceItemDetail("استبدال بطارية S24", 1, 380.0),
            InvoiceItemDetail("عمالة", 1, 470.0)
        )
    ),
    InvoiceItem(
        id = "INV-003",
        invoiceNumber = "INV-20240114-003",
        customerName = "خالد عبدالله",
        customerPhone = "0541112233",
        repairOrderId = "RO-2024-003",
        totalAmount = 320.0,
        paidAmount = 0.0,
        paymentStatus = "OVERDUE",
        paymentMethod = null,
        createdAt = System.currentTimeMillis() - 259200000,
        dueDate = System.currentTimeMillis() - 86400000,
        items = listOf(
            InvoiceItemDetail("منفذ شحن USB-C", 1, 35.0),
            InvoiceItemDetail("عمالة", 1, 285.0)
        )
    ),
    InvoiceItem(
        id = "INV-004",
        invoiceNumber = "INV-20240113-004",
        customerName = "نورة خالد",
        customerPhone = "0534445566",
        repairOrderId = "RO-2024-004",
        totalAmount = 2100.0,
        paidAmount = 2100.0,
        paymentStatus = "PAID",
        paymentMethod = "BANK_TRANSFER",
        createdAt = System.currentTimeMillis() - 345600000,
        dueDate = System.currentTimeMillis() + 86400000,
        items = listOf(
            InvoiceItemDetail("لوحة أم iPhone 14 Pro", 1, 1500.0),
            InvoiceItemDetail("عمالة لحام دقيق", 1, 600.0)
        )
    )
)

data class InvoiceItem(
    val id: String,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val repairOrderId: String?,
    val totalAmount: Double,
    val paidAmount: Double,
    val paymentStatus: String,
    val paymentMethod: String?,
    val createdAt: Long,
    val dueDate: Long,
    val items: List<InvoiceItemDetail>
) {
    val paymentStatusObj: PaymentStatusObj = when (paymentStatus) {
        "PENDING" -> PaymentStatusObj.PENDING
        "PARTIAL" -> PaymentStatusObj.PARTIAL
        "PAID" -> PaymentStatusObj.PAID
        "OVERDUE" -> PaymentStatusObj.OVERDUE
        "REFUNDED" -> PaymentStatusObj.REFUNDED
        "CANCELLED" -> PaymentStatusObj.CANCELLED
        else -> PaymentStatusObj.PENDING
    }
    
    val isOverdue: Boolean get() = dueDate < System.currentTimeMillis() && paymentStatus != "PAID"
}

data class InvoiceItemDetail(
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val total: Double get() = quantity * unitPrice
}

class InvoicesViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading
}