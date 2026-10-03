package com.phonerepair.shop.data.local

import androidx.room.TypeConverter
import com.phonerepair.shop.data.model.*
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun toDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun fromRepairStatus(value: String?): RepairOrder.RepairStatus? =
        value?.let { runCatching { RepairOrder.RepairStatus.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toRepairStatus(value: RepairOrder.RepairStatus?): String? = value?.name

    @TypeConverter
    fun fromPriority(value: String?): RepairOrder.Priority? =
        value?.let { runCatching { RepairOrder.Priority.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toPriority(value: RepairOrder.Priority?): String? = value?.name

    @TypeConverter
    fun fromContactMethod(value: String?): Customer.ContactMethod? =
        value?.let { runCatching { Customer.ContactMethod.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toContactMethod(value: Customer.ContactMethod?): String? = value?.name

    @TypeConverter
    fun fromPartCategory(value: String?): Part.PartCategory? =
        value?.let { runCatching { Part.PartCategory.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toPartCategory(value: Part.PartCategory?): String? = value?.name

    @TypeConverter
    fun fromUnit(value: String?): Part.Unit? =
        value?.let { runCatching { Part.Unit.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toUnit(value: Part.Unit?): String? = value?.name

    @TypeConverter
    fun fromPaymentMethod(value: String?): Invoice.PaymentMethod? =
        value?.let { runCatching { Invoice.PaymentMethod.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toPaymentMethod(value: Invoice.PaymentMethod?): String? = value?.name

    @TypeConverter
    fun fromPaymentStatus(value: String?): Invoice.PaymentStatus? =
        value?.let { runCatching { Invoice.PaymentStatus.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toPaymentStatus(value: Invoice.PaymentStatus?): String? = value?.name

    @TypeConverter
    fun fromStringList(value: String?): List<String> =
        value?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()

    @TypeConverter
    fun toStringList(value: List<String>): String = value.joinToString(SEPARATOR)

    @TypeConverter
    fun fromIntList(value: String?): List<Int> =
        value?.split(SEPARATOR)?.filter { it.isNotBlank() }?.mapNotNull { it.toIntOrNull() } ?: emptyList()

    @TypeConverter
    fun toIntList(value: List<Int>): String = value.joinToString(SEPARATOR)

    @TypeConverter
    fun fromSpecializationList(value: String?): List<Technician.Specialization> =
        value?.split(SEPARATOR)?.filter { it.isNotBlank() }
            ?.mapNotNull { runCatching { Technician.Specialization.valueOf(it) }.getOrNull() }
            ?: emptyList()

    @TypeConverter
    fun toSpecializationList(value: List<Technician.Specialization>): String = value.joinToString(SEPARATOR) { it.name }

    @TypeConverter
    fun fromSkillLevel(value: String?): Technician.SkillLevel? =
        value?.let { runCatching { Technician.SkillLevel.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toSkillLevel(value: Technician.SkillLevel?): String? = value?.name

    @TypeConverter
    fun fromUsedPartList(value: String?): List<UsedPart> =
        value?.split(RECORD_SEPARATOR)?.filter { it.isNotBlank() }?.mapNotNull { it.decodeUsedPart() } ?: emptyList()

    @TypeConverter
    fun toUsedPartList(value: List<UsedPart>): String =
        value.joinToString(RECORD_SEPARATOR) {
            listOf(it.partId, it.partName, it.quantity, it.unitPrice, it.totalPrice).joinToString(FIELD_SEPARATOR)
        }

    @TypeConverter
    fun fromInvoiceItemList(value: String?): List<InvoiceItem> =
        value?.split(RECORD_SEPARATOR)?.filter { it.isNotBlank() }?.mapNotNull { it.decodeInvoiceItem() } ?: emptyList()

    @TypeConverter
    fun toInvoiceItemList(value: List<InvoiceItem>): String =
        value.joinToString(RECORD_SEPARATOR) {
            listOf(it.partId, it.partName, it.quantity, it.unitPrice, it.discount, it.taxRate, it.total)
                .joinToString(FIELD_SEPARATOR)
        }

    @TypeConverter
    fun fromPaymentList(value: String?): List<Payment> =
        value?.split(RECORD_SEPARATOR)?.filter { it.isNotBlank() }?.mapNotNull { it.decodePayment() } ?: emptyList()

    @TypeConverter
    fun toPaymentList(value: List<Payment>): String =
        value.joinToString(RECORD_SEPARATOR) {
            listOf(it.id, it.amount, it.method.name, it.reference ?: "", it.notes ?: "", it.paidAt?.time ?: -1L)
                .joinToString(FIELD_SEPARATOR)
        }

    @TypeConverter
    fun fromTheme(value: String?): AppSettings.Theme? =
        value?.let { runCatching { AppSettings.Theme.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toTheme(value: AppSettings.Theme?): String? = value?.name

    companion object {
        private const val SEPARATOR = "\u001F"
        private const val RECORD_SEPARATOR = "\u001E"
        private const val FIELD_SEPARATOR = "\u001D"
    }
}

private fun String.decodeUsedPart(raw: String): UsedPart? {
    val f = raw.split(FIELD_SEPARATOR_TOKEN)
    if (f.size < 5) return null
    return UsedPart(
        partId = f[0],
        partName = f[1],
        quantity = f[2].toIntOrNull() ?: return null,
        unitPrice = f[3].toDoubleOrNull() ?: return null,
        totalPrice = f[4].toDoubleOrNull() ?: return null
    )
}

private fun String.decodeInvoiceItem(raw: String): InvoiceItem? {
    val f = raw.split(FIELD_SEPARATOR_TOKEN)
    if (f.size < 7) return null
    return InvoiceItem(
        partId = f[0],
        partName = f[1],
        quantity = f[2].toIntOrNull() ?: return null,
        unitPrice = f[3].toDoubleOrNull() ?: return null,
        discount = f[4].toDoubleOrNull() ?: 0.0,
        taxRate = f[5].toDoubleOrNull() ?: 0.0,
        total = f[6].toDoubleOrNull() ?: return null
    )
}

private fun String.decodePayment(raw: String): Payment? {
    val f = raw.split(FIELD_SEPARATOR_TOKEN)
    if (f.size < 6) return null
    return Payment(
        id = f[0],
        amount = f[1].toDoubleOrNull() ?: return null,
        method = runCatching { Invoice.PaymentMethod.valueOf(f[2]) }.getOrNull() ?: return null,
        reference = f[3].ifBlank { null },
        notes = f[4].ifBlank { null },
        paidAt = f[5].toLongOrNull()?.takeIf { it >= 0 }?.let { Date(it) }
    )
}

private const val FIELD_SEPARATOR_TOKEN = "\u001D"