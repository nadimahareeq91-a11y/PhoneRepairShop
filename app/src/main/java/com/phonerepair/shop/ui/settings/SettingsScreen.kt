package com.phonerepair.shop.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var settings by remember { mutableStateOf(AppSettings()) }
    var isLoading by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    LoadingOverlay(isLoading = isLoading) {
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
                    Text("الإعدادات", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("تخصيص تطبيق محل الصيانة", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Profile Section
            SettingsSection(title = "الحساب والمظهر") {
                SettingRow(
                    icon = Icons.Default.Person,
                    title = "الملف الشخصي",
                    subtitle = "تعديل البيانات الشخصية",
                    onClick = { /* navigate to profile */ }
                )
                SettingRow(
                    icon = Icons.Default.Palette,
                    title = "السمة",
                    subtitle = settings.theme.displayName,
                    trailing = {
                        Text(settings.theme.displayName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    onClick = { showThemeDialog = true }
                )
                SettingRow(
                    icon = Icons.Default.Language,
                    title = "اللغة",
                    subtitle = if (settings.language == "ar") "العربية" else "English",
                    trailing = {
                        Text(if (settings.language == "ar") "العربية" else "English", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    onClick = { showLanguageDialog = true }
                )
                SettingRow(
                    icon = Icons.Default.Notifications,
                    title = "الإشعارات",
                    subtitle = "إدارة إشعارات التطبيق",
                    trailing = {
                        Switch(
                            checked = settings.enableNotifications,
                            onCheckedChange = { settings = settings.copy(enableNotifications = it) },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    },
                    onClick = { settings = settings.copy(enableNotifications = !settings.enableNotifications) }
                )
            }

            // Shop Settings
            SettingsSection(title = "إعدادات المحل") {
                SettingRow(
                    icon = Icons.Default.Store,
                    title = "بيانات المحل",
                    subtitle = "الاسم، العنوان، الهاتف، الإيميل",
                    onClick = { /* navigate to shop settings */ }
                )
                SettingRow(
                    icon = Icons.Default.AttachMoney,
                    title = "الضريبة والعملات",
                    subtitle = "نسبة الضريبة: ${(settings.taxRate * 100).toInt()}% • العملة: ${settings.currency}",
                    onClick = { /* navigate to tax settings */ }
                )
                SettingRow(
                    icon = Icons.Default.Schedule,
                    title = "ساعات العمل",
                    subtitle = "${settings.workingHoursStart} - ${settings.workingHoursEnd}",
                    onClick = { /* navigate to working hours */ }
                )
                SettingRow(
                    icon = Icons.Default.Verified,
                    title = "الضمان الافتراضي",
                    subtitle = "${settings.defaultWarrantyDays} يوم",
                    onClick = { /* navigate to warranty settings */ }
                )
            }

            // Printing Section
            SettingsSection(title = "الطباعة") {
                SettingRow(
                    icon = Icons.Default.Print,
                    title = "طابعة الفواتير",
                    subtitle = settings.printerMacAddress?.take(17) ?: "غير متصلة",
                    trailing = {
                        if (settings.printerMacAddress != null) {
                            StatusChip(text = "متصلة", color = Color(0xFF4CAF50), icon = Icons.Default.CheckCircle)
                        } else {
                            StatusChip(text = "غير متصلة", color = MaterialTheme.colorScheme.error, icon = Icons.Default.Block)
                        }
                    },
                    onClick = { /* navigate to printer settings */ }
                )
                SettingRow(
                    icon = Icons.Default.Receipt,
                    title = "تخصيص الفاتورة",
                    subtitle = "العنوان، التذييل، عرض الورق: ${settings.printerPaperWidth}مم",
                    onClick = { /* navigate to receipt settings */ }
                )
            }

            // Data & Sync
            SettingsSection(title = "البيانات والمزامنة") {
                SettingRow(
                    icon = Icons.Default.Backup,
                    title = "النسخ الاحتياطي التلقائي",
                    subtitle = "كل ${settings.backupFrequencyHours} ساعة",
                    trailing = {
                        Switch(
                            checked = settings.autoBackupEnabled,
                            onCheckedChange = { settings = settings.copy(autoBackupEnabled = it) },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    },
                    onClick = { settings = settings.copy(autoBackupEnabled = !settings.autoBackupEnabled) }
                )
                SettingRow(
                    icon = Icons.Default.CloudDownload,
                    title = "استعادة نسخة احتياطية",
                    subtitle = "استعادة البيانات من نسخة محفوظة",
                    onClick = { /* restore backup */ }
                )
                SettingRow(
                    icon = Icons.Default.CloudUpload,
                    title = "مزامنة الآن",
                    subtitle = "مزامنة البيانات مع السحابة",
                    onClick = { /* trigger sync */ }
                )
                SettingRow(
                    icon = Icons.Default.DeleteForever,
                    title = "مسح البيانات المحلية",
                    subtitle = "حذف جميع البيانات المخزنة محلياً",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { /* show confirm dialog */ }
                )
            }

            // Inventory Settings
            SettingsSection(title = "إعدادات المخزون") {
                SettingRow(
                    icon = Icons.Default.Warning,
                    title = "تنبيه المخزون المنخفض",
                    subtitle = "عند وصول الكمية إلى ${settings.lowStockThreshold} قطع",
                    onClick = { /* navigate to low stock settings */ }
                )
            }

            // About & Support
            SettingsSection(title = "حول التطبيق") {
                SettingRow(
                    icon = Icons.Default.Info,
                    title = "إصدار التطبيق",
                    subtitle = "الإصدار 1.0.0 (Build 1)",
                    onClick = { /* show version info */ }
                )
                SettingRow(
                    icon = Icons.Default.Help,
                    title = "المساعدة والدعم",
                    subtitle = "الأسئلة الشائعة، التواصل معنا",
                    onClick = { /* navigate to help */ }
                )
                SettingRow(
                    icon = Icons.Default.PrivacyTip,
                    title = "سياسة الخصوصية",
                    subtitle = "قراءة سياسة الخصوصية",
                    onClick = { /* navigate to privacy policy */ }
                )
                SettingRow(
                    icon = Icons.Default.Description,
                    title = "شروط الاستخدام",
                    subtitle = "قراءة شروط وأحكام الاستخدام",
                    onClick = { /* navigate to terms */ }
                )
            }

            // Danger Zone
            SettingsSection(title = "منطقة الخطر") {
                SettingRow(
                    icon = Icons.Default.Logout,
                    title = "تسجيل الخروج",
                    subtitle = "الخروج من الحساب الحالي",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { /* show logout confirm */ }
                )
                SettingRow(
                    icon = Icons.Default.DeleteForever,
                    title = "حذف الحساب",
                    subtitle = "حذف الحساب وجميع البيانات نهائياً",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { /* show delete account confirm */ }
                )
            }

            // Save Button
            if (settings != AppSettings()) {
                CustomButton(
                    text = "حفظ التغييرات",
                    onClick = { saveSettings() },
                    icon = Icons.Default.Save
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .padding(top = 4.dp))
            content()
        }
    }
}

@Composable
fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit = {},
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    androidx.compose.material3.ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent = {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor)
        },
        supportingContent = {
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        },
        trailingContent = trailing
    )
}

private fun saveSettings() {
    // TODO: Save to DataStore and sync with Firebase
}

data class AppSettings(
    val shopName: String = "محل صيانة الهواتف",
    val shopPhone: String = "",
    val shopAddress: String = "",
    val shopEmail: String = "",
    val taxRate: Double = 0.15,
    val currency: String = "SAR",
    val currencySymbol: String = "ر.س",
    val defaultWarrantyDays: Int = 30,
    val lowStockThreshold: Int = 5,
    val enableNotifications: Boolean = true,
    val enableSmsNotifications: Boolean = false,
    val enableWhatsAppNotifications: Boolean = false,
    val autoBackupEnabled: Boolean = true,
    val backupFrequencyHours: Int = 24,
    val printerMacAddress: String? = null,
    val printerPaperWidth: Int = 58,
    val receiptHeader: String = "شكراً لزيارتكم",
    val receiptFooter: String = "ضمان 30 يوم على القطع المستبدلة",
    val workingHoursStart: String = "09:00",
    val workingHoursEnd: String = "22:00",
    val weekendDays: List<Int> = listOf(6),
    val theme: Theme = Theme.SYSTEM,
    val language: String = "ar"
) {
    enum class Theme(val displayName: String) {
        LIGHT("فاتح"),
        DARK("داكن"),
        SYSTEM("النظام")
    }
}

class SettingsViewModel : androidx.lifecycle.ViewModel() {
    // TODO: Implement settings loading/saving
}