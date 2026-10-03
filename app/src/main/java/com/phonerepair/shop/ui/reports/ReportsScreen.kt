package com.phonerepair.shop.ui.reports

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
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.THIS_MONTH) }
    var reportData by remember { mutableStateOf(ReportData.empty()) }
    var isLoading by remember { mutableStateOf(true) }

    androidx.compose.runtime.LaunchedEffect(selectedPeriod) {
        loadReportData()
    }

    fun loadReportData() {
        isLoading = true
        kotlinx.coroutines.delay(500)
        reportData = ReportData.generateMockData(selectedPeriod)
        isLoading = false
    }

    LoadingOverlay(isLoading = isLoading, message = "جاري تحميل التقارير...") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("التقارير والإحصائيات", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("تحليل أداء المحل", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                androidx.compose.material3.Menu(
                    expanded = { /* handle menu */ },
                    onDismissRequest = { /* handle dismiss */ }
                ) {
                    androidx.compose.material3.DropdownMenu(
                        expanded = true,
                        onDismissRequest = { /* handle dismiss */ }
                    ) {
                        ReportPeriod.values().forEach { period ->
                            androidx.compose.material3.DropdownMenuItem(
                                onClick = { selectedPeriod = period },
                                content = { Text(period.displayName, fontSize = 14.sp) }
                            )
                        }
                    }
                }
            }

            // Period Selector
            PeriodSelector(selectedPeriod = selectedPeriod, onPeriodChange = { selectedPeriod = it })

            // KPI Cards
            SectionHeader(title = "مؤشرات الأداء الرئيسية")
            KPICards(data = reportData)

            // Charts Section
            SectionHeader(title = "الرسوم البيانية")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChartCard(
                    title = "الإيرادات اليومية",
                    subtitle = "مخطط خطي",
                    modifier = Modifier.weight(1f).height(250.dp)
                ) {
                    RevenueLineChart(data = reportData.dailyRevenue)
                }
                
                ChartCard(
                    title = "الطلبات حسب الحالة",
                    subtitle = "مخطط دائري",
                    modifier = Modifier.weight(1f).height(250.dp)
                ) {
                    StatusPieChart(data = reportData.statusDistribution)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChartCard(
                    title = "أعلى القطع مبيعاً",
                    subtitle = "مخطط أعمدة",
                    modifier = Modifier.weight(1f).height(250.dp)
                ) {
                    TopPartsBarChart(data = reportData.topSellingParts)
                }
                
                ChartCard(
                    title = "أداء الفنيين",
                    subtitle = "مخطط أعمدة",
                    modifier = Modifier.weight(1f).height(250.dp)
                ) {
                    TechnicianPerformanceChart(data = reportData.technicianPerformance)
                }
            }

            // Detailed Tables
            SectionHeader(title = "تفاصيل إضافية")
            
            DetailCard(
                title = "إحصائيات المخزون",
                items = reportData.inventoryStats.map { (key, value) ->
                    DetailRow(key, value)
                }
            )
            
            DetailCard(
                title = "إحصائيات العملاء",
                items = reportData.customerStats.map { (key, value) ->
                    DetailRow(key, value)
                }
            )
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onPeriodChange: (ReportPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportPeriod.values().forEach { period ->
            val isSelected = selectedPeriod == period
            androidx.compose.material3.Chip(
                selected = isSelected,
                onClick = { onPeriodChange(period) },
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(period.displayName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun KPICards(data: ReportData) {
    val kpis = listOf(
        KPIItem("إجمالي الإيرادات", formatCurrency(data.totalRevenue), Icons.Default.AttachMoney, Color(0xFF2E7D32), data.revenueChange),
        KPIItem("إجمالي الطلبات", data.totalOrders.toString(), Icons.Default.Assignment, MaterialTheme.colorScheme.primary, data.ordersChange),
        KPIItem("متوسط قيمة الطلب", formatCurrency(data.avgOrderValue), Icons.Default.TrendingUp, Color(0xFF9C27B0), data.avgOrderChange),
        KPIItem("معدل الإكمال", "${data.completionRate.toInt()}%", Icons.Default.CheckCircle, Color(0xFF4CAF50), data.completionChange),
        KPIItem("عملاء جدد", data.newCustomers.toString(), Icons.Default.PersonAdd, Color(0xFF2196F3), data.newCustomersChange),
        KPIItem("نقاط الولاء الممنوحة", data.loyaltyPointsAwarded.toString(), Icons.Default.Star, Color(0xFFFFB300), data.loyaltyChange)
    )

    androidx.compose.foundation.lazy.LazyHorizontalGrid(
        rows = androidx.compose.foundation.lazy.GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(kpis) { kpi ->
            KPICard(item = kpi)
        }
    }
}

@Composable
fun KPICard(item: KPIItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = item.color.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(item.value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = item.color)
                        Text(item.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = item.color.copy(alpha = 0.3f)
                        )
                    }
                }
                
                // Change indicator
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (item.change >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                    Text(
                        "${if (item.change >= 0) "+" else ""}${item.change.toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (item.change >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                    Text("من الفترة السابقة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            // Chart Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun RevenueLineChart(data: List<ChartDataPoint>) {
    // Simple line chart implementation
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("مخطط الإيرادات - ${data.size} يوم", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // TODO: Implement actual chart using MPAndroidChart or Compose Canvas
    }
}

@Composable
fun StatusPieChart(data: List<ChartDataPoint>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("توزيع الحالات - ${data.size} حالة", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // TODO: Implement actual pie chart
    }
}

@Composable
fun TopPartsBarChart(data: List<ChartDataPoint>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.take(5).forEach { point ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(point.label, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                    Text("${point.value.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TechnicianPerformanceChart(data: List<ChartDataPoint>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.forEach { point ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(point.label, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                    Text("${point.value.toInt()} طلب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailCard(title: String, items: List<DetailRow>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(row.label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(row.value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

data class DetailRow(val label: String, val value: String)

data class KPIItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val change: Float
)

data class ChartDataPoint(
    val label: String,
    val value: Float,
    val color: Color = MaterialTheme.colorScheme.primary
)

enum class ReportPeriod(val displayName: String) {
    TODAY("اليوم"),
    THIS_WEEK("هذا الأسبوع"),
    THIS_MONTH("هذا الشهر"),
    LAST_MONTH("الشهر الماضي"),
    THIS_YEAR("هذا العام"),
    CUSTOM("مخصص")
}

data class ReportData(
    val totalRevenue: Double,
    val totalOrders: Int,
    val avgOrderValue: Double,
    val completionRate: Float,
    val newCustomers: Int,
    val loyaltyPointsAwarded: Int,
    val revenueChange: Float,
    val ordersChange: Float,
    val avgOrderChange: Float,
    val completionChange: Float,
    val newCustomersChange: Float,
    val loyaltyChange: Float,
    val dailyRevenue: List<ChartDataPoint>,
    val statusDistribution: List<ChartDataPoint>,
    val topSellingParts: List<ChartDataPoint>,
    val technicianPerformance: List<ChartDataPoint>,
    val inventoryStats: Map<String, String>,
    val customerStats: Map<String, String>
) {
    companion object {
        fun empty(): ReportData = ReportData(
            totalRevenue = 0.0,
            totalOrders = 0,
            avgOrderValue = 0.0,
            completionRate = 0f,
            newCustomers = 0,
            loyaltyPointsAwarded = 0,
            revenueChange = 0f,
            ordersChange = 0f,
            avgOrderChange = 0f,
            completionChange = 0f,
            newCustomersChange = 0f,
            loyaltyChange = 0f,
            dailyRevenue = emptyList(),
            statusDistribution = emptyList(),
            topSellingParts = emptyList(),
            technicianPerformance = emptyList(),
            inventoryStats = emptyMap(),
            customerStats = emptyMap()
        )
        
        fun generateMockData(period: ReportPeriod): ReportData {
            val days = when (period) {
                ReportPeriod.TODAY -> 1
                ReportPeriod.THIS_WEEK -> 7
                ReportPeriod.THIS_MONTH -> 30
                ReportPeriod.LAST_MONTH -> 30
                ReportPeriod.THIS_YEAR -> 365
                ReportPeriod.CUSTOM -> 30
            }
            
            return ReportData(
                totalRevenue = 157500.0,
                totalOrders = 342,
                avgOrderValue = 460.5,
                completionRate = 87.5f,
                newCustomers = 28,
                loyaltyPointsAwarded = 12500,
                revenueChange = 12.5f,
                ordersChange = 8.2f,
                avgOrderChange = 3.1f,
                completionChange = 2.3f,
                newCustomersChange = 15.7f,
                loyaltyChange = 22.4f,
                dailyRevenue = (1..days).map { day ->
                    ChartDataPoint(
                        label = "${day}/${Calendar.getInstance().get(Calendar.MONTH) + 1}",
                        value = (3000 + (day * 120) + (Math.random() * 500)).toFloat()
                    )
                },
                statusDistribution = listOf(
                    ChartDataPoint("تم التسليم", 145f, Color(0xFF4CAF50)),
                    ChartDataPoint("قيد الإصلاح", 67f, Color(0xFF3F51B5)),
                    ChartDataPoint("جاهز للاستلام", 42f, Color(0xFF2196F3)),
                    ChartDataPoint("بانتظار القطع", 28f, Color(0xFF9C27B0)),
                    ChartDataPoint("قيد التشخيص", 35f, Color(0xFFFF9800)),
                    ChartDataPoint("ملغي", 12f, MaterialTheme.colorScheme.error),
                    ChartDataPoint("معلق", 13f, Color(0xFF607D8B))
                ),
                topSellingParts = listOf(
                    ChartDataPoint("شاشة iPhone 15 Pro", 45f),
                    ChartDataPoint("بطارية iPhone 15", 67f),
                    ChartDataPoint("منفذ شحن USB-C", 89f),
                    ChartDataPoint("كاميرا S24 Ultra", 23f),
                    ChartDataPoint("شاشة iPhone 13", 34f),
                    ChartDataPoint("بطارية Samsung", 56f),
                    ChartDataPoint("سماعة أذن iPhone", 78f)
                ),
                technicianPerformance = listOf(
                    ChartDataPoint("محمد علي", 89f),
                    ChartDataPoint("فاطمة سالم", 76f),
                    ChartDataPoint("أحمد حسن", 92f),
                    ChartDataPoint("سارة محمد", 68f),
                    ChartDataPoint("خالد أحمد", 45f)
                ),
                inventoryStats = mapOf(
                    "إجمالي القطع" to "1,245",
                    "قيمة المخزون" to formatCurrency(285000.0),
                    "قطع منخفضة" to "12",
                    "قطع منتهية" to "3",
                    "الفئات النشطة" to "14"
                ),
                customerStats = mapOf(
                    "إجمالي العملاء" to "1,245",
                    "عملاء نشطين (30 يوم)" to "342",
                    "عملاء VIP" to "87",
                    "متوسط الطلبات/عميل" to "2.7",
                    "معدل الاحتفاظ" to "78%"
                )
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("ar", "SA")).format(amount)
}

class ReportsViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement data loading
}