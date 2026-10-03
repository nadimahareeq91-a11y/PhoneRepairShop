package com.phonerepair.shop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable
import java.io.Serializable
import java.util.*

@Serializable
@Entity(tableName = "technicians")
data class Technician(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val specialization: List<Specialization> = emptyList(),
    val skillLevel: SkillLevel = SkillLevel.JUNIOR,
    val hireDate: Date? = null,
    val salary: Double = 0.0,
    val commissionRate: Double = 0.0,
    val isActive: Boolean = true,
    val avatarUrl: String? = null,
    val notes: String? = null,
    val completedOrders: Int = 0,
    val rating: Double = 0.0,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
    val isSynced: Boolean = false
) : Serializable {
    enum class Specialization(val displayName: String) {
        SCREEN_REPLACEMENT("استبدال شاشات"),
        BOARD_REPAIR("إصلاح لوحات أم"),
        MICROSOLDERING("لحام دقيق"),
        DATA_RECOVERY("استعادة بيانات"),
        WATER_DAMAGE("أضرار مياه"),
        SOFTWARE("برمجيات"),
        DIAGNOSTICS("تشخيص"),
        GENERAL("عام")
    }

    enum class SkillLevel(val displayName: String, val color: Int, val baseCommission: Double) {
        JUNIOR("مبتدئ", 0xFF4CAF50, 0.05),
        INTERMEDIATE("متوسط", 0xFF2196F3, 0.10),
        SENIOR("خبير", 0xFFFF9800, 0.15),
        MASTER("ماستر", 0xFF9C27B0, 0.20)
    }

    fun canHandle(specialization: Specialization): Boolean = this.specialization.contains(specialization) || this.specialization.contains(Specialization.GENERAL)
}