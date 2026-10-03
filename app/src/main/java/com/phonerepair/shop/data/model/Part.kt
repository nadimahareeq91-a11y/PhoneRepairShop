package com.phonerepair.shop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable
import java.io.Serializable
import java.util.*

@Serializable
@Entity(tableName = "parts")
data class Part(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val barcode: String? = null,
    val sku: String,
    val name: String,
    val nameAr: String? = null,
    val category: PartCategory,
    val compatibleDevices: List<String> = emptyList(),
    val purchasePrice: Double,
    val salePrice: Double,
    val wholesalePrice: Double? = null,
    val currentStock: Int = 0,
    val minStockLevel: Int = 5,
    val maxStockLevel: Int = 100,
    val unit: Unit = Unit.PIECE,
    val supplierId: String? = null,
    val supplierName: String? = null,
    val location: String? = null,
    val description: String? = null,
    val images: List<String> = emptyList(),
    val isActive: Boolean = true,
    val isSerialized: Boolean = false,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
    val lastRestockedAt: Date? = null,
    val isSynced: Boolean = false
) : Serializable {
    enum class PartCategory(val displayName: String) {
        SCREEN("شاشات"),
        BATTERY("بطاريات"),
        CAMERA("كاميرات"),
        CHARGING_PORT("منافذ شحن"),
        MOTHERBOARD("لوحة أم"),
        BUTTONS("أزرار"),
        SPEAKER("سماعات"),
        MICROPHONE("ميكروفون"),
        VIBRATOR("محرك اهتزاز"),
        FLEX_CABLE("كابلات فليكس"),
        ADHESIVE("مادة لاصقة"),
        TOOL("أدوات"),
        CASE("أغطية"),
        ACCESSORY("إكسسوارات"),
        OTHER("أخرى")
    }

    enum class Unit(val displayName: String) {
        PIECE("قطعة"),
        PACK("علبة"),
        SET("مجموعة"),
        METER("متر"),
        KG("كيلو")
    }

    fun isLowStock(): Boolean = currentStock <= minStockLevel
    fun isOutOfStock(): Boolean = currentStock <= 0
    fun getStockStatus(): StockStatus = when {
        currentStock <= 0 -> StockStatus.OUT_OF_STOCK
        currentStock <= minStockLevel -> StockStatus.LOW
        currentStock >= maxStockLevel -> StockStatus.OVERSTOCK
        else -> StockStatus.NORMAL
    }

    enum class StockStatus(val displayName: String, val color: Int) {
        NORMAL("طبيعي", 0xFF4CAF50),
        LOW("منخفض", 0xFFFF9800),
        OUT_OF_STOCK("نفد", 0xFFF44336),
        OVERSTOCK("زائد", 0xFF2196F3)
    }

    fun getProfitMargin(): Double = ((salePrice - purchasePrice) / purchasePrice) * 100
}