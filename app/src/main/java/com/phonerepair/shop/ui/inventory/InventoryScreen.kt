package com.phonerepair.shop.ui.inventory

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
fun InventoryScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: InventoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showLowStockOnly by remember { mutableStateOf(false) }
    var parts by remember { mutableStateOf<List<PartItem>>(sampleParts) }
    var isLoading by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showAddPart by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

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
                            Text("إدارة المخزون", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${parts.size} قطعة • ${parts.count { it.isLowStock }} منخفضة", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomIconButton(
                                onClick = { showBarcodeScanner = true },
                                icon = Icons.Default.QrCodeScanner,
                                contentDescription = "مسح باركود"
                            )
                            CustomIconButton(
                                onClick = { showFilters = !showFilters },
                                icon = if (showFilters) Icons.Default.FilterList else Icons.Outlined.FilterList,
                                contentDescription = "تصفية"
                            )
                            CustomButton(
                                text = "قطعة جديدة",
                                onClick = { showAddPart = true },
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
                        placeholder = "ابحث بالاسم، SKU، الباركود...",
                        leadingIcon = Icons.Default.Search,
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingIconClick = { searchQuery = "" }
                    )
                }
            }

            // Stats Row
            StatsRow(parts = parts)

            // Filter Chips
            if (showFilters) {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    showLowStockOnly = showLowStockOnly,
                    onLowStockChange = { showLowStockOnly = it }
                )
            }

            // Parts List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                items(parts.filter { part ->
                    (searchQuery.isBlank() || 
                     part.name.contains(searchQuery, ignoreCase = true) ||
                     part.sku.contains(searchQuery, ignoreCase = true) ||
                     part.barcode?.contains(searchQuery, ignoreCase = true) == true) &&
                    (selectedCategory == null || part.category == selectedCategory) &&
                    (!showLowStockOnly || part.isLowStock)
                }) { part ->
                    PartCard(
                        part = part,
                        onClick = { onNavigateToDetail(part.id) },
                        onStockAdjust = { adjustment ->
                            parts = parts.map { if (it.id == part.id) it.copy(currentStock = it.currentStock + adjustment) else it }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatsRow(parts: List<PartItem>) {
    val totalValue = parts.sumOf { it.currentStock * it.salePrice }
    val lowStockCount = parts.count { it.isLowStock }
    val outOfStockCount = parts.count { it.currentStock <= 0 }
    val categoriesCount = parts.map { it.category }.distinct().size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox("إجمالي القطع", "${parts.size}", Icons.Default.Inventory, MaterialTheme.colorScheme.primary)
        StatBox("قيمة المخزون", formatCurrency(totalValue), Icons.Default.AttachMoney, Color(0xFF2E7D32))
        StatBox("منخفضة", lowStockCount.toString(), Icons.Default.Warning, Color(0xFFFF9800))
        StatBox("منتهية", outOfStockCount.toString(), Icons.Default.Block, MaterialTheme.colorScheme.error)
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
                    Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
                }
                Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategory: String?,
    onCategoryChange: (String?) -> Unit,
    showLowStockOnly: Boolean,
    onLowStockChange: (Boolean) -> Unit
) {
    val categories = listOf(
        null to "الكل",
        "الشاشات" to "الشاشات",
        "البطاريات" to "البطاريات",
        "الكاميرات" to "الكاميرات",
        "منافذ الشحن" to "منافذ الشحن",
        "اللوحة الأم" to "اللوحة الأم",
        "الأزرار" to "الأزرار",
        "السماعات" to "السماعات",
        "أخرى" to "أخرى"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(
                checked = showLowStockOnly,
                onCheckedChange = onLowStockChange,
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.error
                )
            )
            Text("منخفضة المخزون فقط", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        }
        
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (value, label) ->
                val isSelected = selectedCategory == value
                androidx.compose.material3.FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(value) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = androidx.compose.material3.FilterChipDefaults.colors(
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
fun PartCard(
    part: PartItem,
    onClick: () -> Unit,
    onStockAdjust: (Int) -> Unit
) {
    val stockStatus = part.stockStatus
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = when (stockStatus) {
                PartItem.StockStatus.LOW -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
                PartItem.StockStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(part.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                        if (part.barcode != null) {
                            androidx.compose.material3.Chip(
                                modifier = Modifier.height(20.dp),
                                onClick = { /* copy barcode */ },
                                colors = androidx.compose.material3.ChipDefaults.chipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("باركود", fontSize = 9.sp)
                            }
                        }
                    }
                    Text("SKU: ${part.sku}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                StatusChip(
                    text = stockStatus.displayName,
                    color = Color(stockStatus.color),
                    icon = stockStatus.icon
                )
            }
            
            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    icon = Icons.Default.Inventory,
                    label = "المخزون",
                    value = "${part.currentStock} ${part.unit}",
                    valueColor = if (part.isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                InfoItem(
                    icon = Icons.Default.ShoppingCart,
                    label = "سعر الشراء",
                    value = formatCurrency(part.purchasePrice)
                )
                InfoItem(
                    icon = Icons.Default.Sell,
                    label = "سعر البيع",
                    value = formatCurrency(part.salePrice),
                    valueColor = Color(0xFF2E7D32)
                )
                InfoItem(
                    icon = Icons.Default.Percent,
                    label = "الربح",
                    value = "${part.profitMargin.toInt()}%",
                    valueColor = Color(0xFF9C27B0)
                )
            }
            
            // Supplier & Location
            if (part.supplierName != null || part.location != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (part.supplierName != null) {
                        InfoItem(
                            icon = Icons.Default.LocalShipping,
                            label = "المورد",
                            value = part.supplierName!!
                        )
                    }
                    if (part.location != null) {
                        InfoItem(
                            icon = Icons.Default.LocationOn,
                            label = "الموقع",
                            value = part.location!!
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
                    text = "-",
                    onClick = { onStockAdjust(-1) },
                    modifier = Modifier.width(50.dp).height(36.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
                CustomButton(
                    text = "+",
                    onClick = { onStockAdjust(1) },
                    modifier = Modifier.width(50.dp).height(36.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f),
                        contentColor = Color(0xFF2E7D32)
                    )
                )
                CustomButton(
                    text = "تعديل",
                    onClick = { /* show adjust dialog */ },
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
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
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

private fun formatCurrency(amount: Double): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("ar", "SA")).format(amount)
}

// Sample Data
val sampleParts = listOf(
    PartItem(
        id = "PART-001",
        barcode = "1234567890123",
        sku = "SCR-IP15PM-BLK",
        name = "شاشة iPhone 15 Pro Max أصلية",
        nameAr = "شاشة آيفون 15 برو ماكس أصلية",
        category = "الشاشات",
        compatibleDevices = listOf("iPhone 15 Pro Max"),
        purchasePrice = 450.0,
        salePrice = 750.0,
        currentStock = 3,
        minStockLevel = 5,
        maxStockLevel = 20,
        unit = "قطعة",
        supplierName = "شركة الشاشات الذهبية",
        location = "رف أ-1",
        isActive = true
    ),
    PartItem(
        id = "PART-002",
        barcode = "1234567890124",
        sku = "BAT-IP15-STD",
        name = "بطارية iPhone 15 أصلية",
        nameAr = "بطارية آيفون 15 أصلية",
        category = "البطاريات",
        compatibleDevices = listOf("iPhone 15", "iPhone 15 Plus"),
        purchasePrice = 85.0,
        salePrice = 180.0,
        currentStock = 12,
        minStockLevel = 10,
        maxStockLevel = 50,
        unit = "قطعة",
        supplierName = "مورد البطاريات المعتمد",
        location = "رف ب-3",
        isActive = true
    ),
    PartItem(
        id = "PART-003",
        barcode = "1234567890125",
        sku = "CAM-S24U-MAIN",
        name = "كاميرا خلفية رئيسية S24 Ultra",
        nameAr = "كاميرا خلفية رئيسية اس 24 الترا",
        category = "الكاميرات",
        compatibleDevices = listOf("Galaxy S24 Ultra"),
        purchasePrice = 220.0,
        salePrice = 380.0,
        currentStock = 0,
        minStockLevel = 3,
        maxStockLevel = 15,
        unit = "قطعة",
        supplierName = "شركة الكاميرات المتطورة",
        location = "رف ج-2",
        isActive = true
    ),
    PartItem(
        id = "PART-004",
        barcode = "1234567890126",
        sku = "PORT-USB-C-UNI",
        name = "منفذ شحن USB-C عام",
        nameAr = "منفذ شحن يو اس بي سي عام",
        category = "منافذ الشحن",
        compatibleDevices = listOf("Android", "iPhone 15+"),
        purchasePrice = 12.0,
        salePrice = 35.0,
        currentStock = 45,
        minStockLevel = 20,
        maxStockLevel = 100,
        unit = "قطعة",
        supplierName = "مورد الإكسسوارات",
        location = "درج 1",
        isActive = true
    ),
    PartItem(
        id = "PART-005",
        barcode = "1234567890127",
        sku = "SCR-IP13-BLK",
        name = "شاشة iPhone 13 أصلية",
        nameAr = "شاشة آيفون 13 أصلية",
        category = "الشاشات",
        compatibleDevices = listOf("iPhone 13", "iPhone 13 Pro"),
        purchasePrice = 280.0,
        salePrice = 480.0,
        currentStock = 2,
        minStockLevel = 5,
        maxStockLevel = 20,
        unit = "قطعة",
        supplierName = "شركة الشاشات الذهبية",
        location = "رف أ-2",
        isActive = true
    )
)

data class PartItem(
    val id: String,
    val barcode: String?,
    val sku: String,
    val name: String,
    val nameAr: String?,
    val category: String,
    val compatibleDevices: List<String>,
    val purchasePrice: Double,
    val salePrice: Double,
    val currentStock: Int,
    val minStockLevel: Int,
    val maxStockLevel: Int,
    val unit: String,
    val supplierName: String?,
    val location: String?,
    val isActive: Boolean
) {
    val isLowStock: Boolean get() = currentStock <= minStockLevel && currentStock > 0
    val isOutOfStock: Boolean get() = currentStock <= 0
    val profitMargin: Double get() = if (purchasePrice > 0) ((salePrice - purchasePrice) / purchasePrice) * 100 else 0.0
    
    val stockStatus: StockStatus get() = when {
        currentStock <= 0 -> StockStatus.OUT_OF_STOCK
        currentStock <= minStockLevel -> StockStatus.LOW
        currentStock >= maxStockLevel -> StockStatus.OVERSTOCK
        else -> StockStatus.NORMAL
    }
    
    enum class StockStatus(val displayName: String, val color: Color, val icon: ImageVector) {
        NORMAL("طبيعي", Color(0xFF4CAF50), Icons.Default.CheckCircle),
        LOW("منخفض", Color(0xFFFF9800), Icons.Default.Warning),
        OUT_OF_STOCK("نفد", Icons.Default.Block, MaterialTheme.colorScheme.error),
        OVERSTOCK("زائد", Color(0xFF2196F3), Icons.Default.TrendingUp)
    }
}

class InventoryViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading
}