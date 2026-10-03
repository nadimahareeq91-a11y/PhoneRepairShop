package com.phonerepair.shop.data.local

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.phonerepair.shop.data.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Date? = value?.toDate()

    @TypeConverter
    fun toTimestamp(value: Date?): Timestamp? = value?.let { Timestamp(it) }

    @TypeConverter
    fun fromDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun toDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun fromRepairStatus(value: String?): RepairOrder.RepairStatus? = value?.let { RepairOrder.RepairStatus.valueOf(it) }

    @TypeConverter
    fun toRepairStatus(value: RepairOrder.RepairStatus?): String? = value?.name

    @TypeConverter
    fun fromPriority(value: String?): RepairOrder.Priority? = value?.let { RepairOrder.Priority.valueOf(it) }

    @TypeConverter
    fun toPriority(value: RepairOrder.Priority?): String? = value?.name

    @TypeConverter
    fun fromContactMethod(value: String?): Customer.ContactMethod? = value?.let { Customer.ContactMethod.valueOf(it) }

    @TypeConverter
    fun toContactMethod(value: Customer.ContactMethod?): String? = value?.name

    @TypeConverter
    fun fromPartCategory(value: String?): Part.PartCategory? = value?.let { Part.PartCategory.valueOf(it) }

    @TypeConverter
    fun toPartCategory(value: Part.PartCategory?): String? = value?.name

    @TypeConverter
    fun fromUnit(value: String?): Part.Unit? = value?.let { Part.Unit.valueOf(it) }

    @TypeConverter
    fun toUnit(value: Part.Unit?): String? = value?.name

    @TypeConverter
    fun fromPaymentMethod(value: String?): Invoice.PaymentMethod? = value?.let { Invoice.PaymentMethod.valueOf(it) }

    @TypeConverter
    fun toPaymentMethod(value: Invoice.PaymentMethod?): String? = value?.name

    @TypeConverter
    fun fromPaymentStatus(value: String?): Invoice.PaymentStatus? = value?.let { Invoice.PaymentStatus.valueOf(it) }

    @TypeConverter
    fun toPaymentStatus(value: Invoice.PaymentStatus?): String? = value?.name

    @TypeConverter
    fun fromSpecializationList(value: String?): List<Technician.Specialization> =
        value?.let { json.decodeFromString<List<Technician.Specialization>>(it) } ?: emptyList()

    @TypeConverter
    fun toSpecializationList(value: List<Technician.Specialization>): String = json.encodeToString(value)

    @TypeConverter
    fun fromSkillLevel(value: String?): Technician.SkillLevel? = value?.let { Technician.SkillLevel.valueOf(it) }

    @TypeConverter
    fun toSkillLevel(value: Technician.SkillLevel?): String? = value?.name

    @TypeConverter
    fun fromUsedPartList(value: String?): List<UsedPart> =
        value?.let { json.decodeFromString<List<UsedPart>>(it) } ?: emptyList()

    @TypeConverter
    fun toUsedPartList(value: List<UsedPart>): String = json.encodeToString(value)

    @TypeConverter
    fun fromInvoiceItemList(value: String?): List<InvoiceItem> =
        value?.let { json.decodeFromString<List<InvoiceItem>>(it) } ?: emptyList()

    @TypeConverter
    fun toInvoiceItemList(value: List<InvoiceItem>): String = json.encodeToString(value)

    @TypeConverter
    fun fromPaymentList(value: String?): List<Payment> =
        value?.let { json.decodeFromString<List<Payment>>(it) } ?: emptyList()

    @TypeConverter
    fun toPaymentList(value: List<Payment>): String = json.encodeToString(value)

    @TypeConverter
    fun fromStringList(value: String?): List<String> =
        value?.let { json.decodeFromString<List<String>>(it) } ?: emptyList()

    @TypeConverter
    fun toStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun fromIntList(value: String?): List<Int> =
        value?.let { json.decodeFromString<List<Int>>(it) } ?: emptyList()

    @TypeConverter
    fun toIntList(value: List<Int>): String = json.encodeToString(value)

    @TypeConverter
    fun fromTheme(value: String?): AppSettings.Theme? = value?.let { AppSettings.Theme.valueOf(it) }

    @TypeConverter
    fun toTheme(value: AppSettings.Theme?): String? = value?.name
}