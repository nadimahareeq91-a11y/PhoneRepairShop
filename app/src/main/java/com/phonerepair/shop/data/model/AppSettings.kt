package com.phonerepair.shop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: String = "default",
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
    val weekendDays: List<Int> = listOf(6), // Friday
    val theme: Theme = Theme.SYSTEM,
    val language: String = "ar"
) {
    enum class Theme(val displayName: String) {
        LIGHT("فاتح"),
        DARK("داكن"),
        SYSTEM("النظام")
    }
}