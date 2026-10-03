package com.phonerepair.shop.data.model

import androidx.compose.foundation.lazy.items
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val repairOrderId: String,
    val customerId: String,
    val customerName: String,
    val items: List<InvoiceItem>,
    val subtotal: Double,
    val taxAmount: Double,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val payments: List<Payment> = emptyList(),
    val notes: String? = null,
    val terms: String? = null,
    val dueDate: Date? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val printedAt: Date? = null,
    val isSynced: Boolean = false
) {
    enum class PaymentMethod(val displayName: String, val icon: String) {
        CASH("نقدي", "cash"),
        CARD("بطاقة", "card"),
        BANK_TRANSFER("تحويل بنكي", "bank"),
        WALLET("محفظة إلكترونية", "wallet"),
        INSTALLMENTS("أقساط", "installments"),
        OTHER("أخرى", "other")
    }

    enum class PaymentStatus(val displayName: String, val color: Long) {
        PENDING("معلق", 0xFFFF9800),
        PARTIAL("مدفوع جزئياً", 0xFF2196F3),
        PAID("مدفوع بالكامل", 0xFF4CAF50),
        OVERDUE("متأخر", 0xFFF44336),
        REFUNDED("مسترد", 0xFF9C27B0),
        CANCELLED("ملغي", 0xFF607D8B)
    }

    fun getRemainingAmount(): Double = totalAmount - paidAmount
    fun isOverdue(): Boolean = dueDate?.before(Date()) == true && paymentStatus != PaymentStatus.PAID

    companion object {
        fun generateInvoiceNumber(): String {
            val now = Date()
            val format = java.text.SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            return "INV-${format.format(now)}"
        }
    }
}
data class InvoiceItem(
    val partId: String,
    val partName: String,
    val quantity: Int,
    val unitPrice: Double,
    val discount: Double = 0.0,
    val taxRate: Double = 0.15,
    val total: Double
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "partId" to partId,
        "partName" to partName,
        "quantity" to quantity,
        "unitPrice" to unitPrice,
        "discount" to discount,
        "taxRate" to taxRate,
        "total" to total
    )
}
data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val method: Invoice.PaymentMethod,
    val reference: String? = null,
    val notes: String? = null,
    val paidAt: Date? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "amount" to amount,
        "method" to method.name,
        "reference" to reference,
        "notes" to notes,
        "paidAt" to paidAt?.time
    )
}