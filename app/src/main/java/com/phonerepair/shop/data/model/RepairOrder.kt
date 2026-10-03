package com.phonerepair.shop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "repair_orders")
data class RepairOrder(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deviceBrand: String,
    val deviceModel: String,
    val deviceSerialNumber: String? = null,
    val deviceColor: String? = null,
    val deviceCondition: String? = null,
    val reportedIssue: String,
    val diagnosedIssue: String? = null,
    val status: RepairStatus = RepairStatus.RECEIVED,
    val priority: Priority = Priority.NORMAL,
    val technicianId: String? = null,
    val technicianName: String? = null,
    val estimatedCost: Double = 0.0,
    val finalCost: Double = 0.0,
    val paidAmount: Double = 0.0,
    val partsUsed: List<UsedPart> = emptyList(),
    val notes: String? = null,
    val images: List<String> = emptyList(),
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val completedAt: Date? = null,
    val warrantyDays: Int = 30,
    val isSynced: Boolean = false
) {
    enum class RepairStatus(val displayName: String, val color: Long) {
        RECEIVED("تم الاستلام", 0xFF2196F3),
        DIAGNOSING("قيد التشخيص", 0xFFFF9800),
        WAITING_PARTS("بانتظار القطع", 0xFF9C27B0),
        IN_REPAIR("قيد الإصلاح", 0xFF3F51B5),
        QUALITY_CHECK("فحص الجودة", 0xFF009688),
        READY_FOR_PICKUP("جاهز للاستلام", 0xFF4CAF50),
        DELIVERED("تم التسليم", 0xFF8BC34A),
        CANCELLED("ملغي", 0xFFF44336),
        ON_HOLD("معلق", 0xFF607D8B)
    }

    enum class Priority(val displayName: String, val color: Long) {
        LOW("منخفضة", 0xFF4CAF50),
        NORMAL("عادية", 0xFF2196F3),
        HIGH("عالية", 0xFFFF9800),
        URGENT("عاجلة", 0xFFF44336)
    }

    fun getRemainingAmount(): Double = finalCost - paidAmount
    fun isFullyPaid(): Boolean = paidAmount >= finalCost
    fun getStatusDisplayName(): String = status.displayName
    fun getPriorityDisplayName(): String = priority.displayName
}
data class UsedPart(
    val partId: String,
    val partName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "partId" to partId,
        "partName" to partName,
        "quantity" to quantity,
        "unitPrice" to unitPrice,
        "totalPrice" to totalPrice
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): UsedPart = UsedPart(
            partId = map["partId"] as String,
            partName = map["partName"] as String,
            quantity = map["quantity"] as Int,
            unitPrice = (map["unitPrice"] as Number).toDouble(),
            totalPrice = (map["totalPrice"] as Number).toDouble()
        )
    }
}