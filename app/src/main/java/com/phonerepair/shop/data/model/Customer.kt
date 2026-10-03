package com.phonerepair.shop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val nationalId: String? = null,
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val loyaltyPoints: Int = 0,
    val preferredContactMethod: ContactMethod = ContactMethod.PHONE,
    val notes: String? = null,
    val isBlacklisted: Boolean = false,
    val blacklistReason: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val lastVisitAt: Date? = null,
    val isSynced: Boolean = false
) {
    enum class ContactMethod(val displayName: String) {
        PHONE("هاتف"),
        SMS("رسائل نصية"),
        WHATSAPP("واتساب"),
        EMAIL("بريد إلكتروني")
    }

    fun getDisplayName(): String = name
    fun getContactInfo(): String = when (preferredContactMethod) {
        ContactMethod.PHONE -> phone
        ContactMethod.SMS -> phone
        ContactMethod.WHATSAPP -> phone
        ContactMethod.EMAIL -> email ?: phone
    }
}