package com.phonerepair.shop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.phonerepair.shop.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: RepairOrder): Long

    @Update
    suspend fun update(order: RepairOrder): Int

    @Query("DELETE FROM repair_orders WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM repair_orders ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE id = :id")
    fun getById(id: String): Flow<RepairOrder?>

    @Query("SELECT * FROM repair_orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getByCustomerId(customerId: String): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE technicianId = :technicianId ORDER BY createdAt DESC")
    fun getByTechnicianId(technicianId: String): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE createdAt BETWEEN :start AND :end ORDER BY createdAt DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE customerName LIKE :query OR deviceModel LIKE :query OR reportedIssue LIKE :query ORDER BY createdAt DESC")
    fun search(query: String): Flow<List<RepairOrder>>

    @Query("SELECT * FROM repair_orders WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<RepairOrder>>

    @Query("UPDATE repair_orders SET status = :status, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun updateStatus(id: String, status: String): Int

    @Query("UPDATE repair_orders SET technicianId = :technicianId, technicianName = :technicianName, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun assignTechnician(id: String, technicianId: String, technicianName: String): Int

    @Query("UPDATE repair_orders SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String): Int

    @Transaction
    @Query("SELECT * FROM repair_orders WHERE id = :id")
    suspend fun getWithDetails(id: String): RepairOrder?
}

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer): Int

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAll(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getById(id: String): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE phone = :phone")
    fun getByPhone(phone: String): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE name LIKE :query OR phone LIKE :query OR email LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<Customer>>

    @Query("UPDATE customers SET totalOrders = totalOrders + 1, totalSpent = totalSpent + :amount, lastVisitAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun incrementStats(id: String, amount: Double): Int

    @Query("UPDATE customers SET loyaltyPoints = loyaltyPoints + :points WHERE id = :id")
    suspend fun addLoyaltyPoints(id: String, points: Int): Int

    @Query("UPDATE customers SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String): Int
}

@Dao
interface PartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(part: Part): Long

    @Update
    suspend fun update(part: Part): Int

    @Query("DELETE FROM parts WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM parts WHERE isActive = 1 ORDER BY name ASC")
    fun getAll(): Flow<List<Part>>

    @Query("SELECT * FROM parts WHERE id = :id")
    fun getById(id: String): Flow<Part?>

    @Query("SELECT * FROM parts WHERE barcode = :barcode")
    fun getByBarcode(barcode: String): Flow<Part?>

    @Query("SELECT * FROM parts WHERE category = :category AND isActive = 1 ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<Part>>

    @Query("SELECT * FROM parts WHERE currentStock <= minStockLevel AND isActive = 1 ORDER BY currentStock ASC")
    fun getLowStock(): Flow<List<Part>>

    @Query("SELECT * FROM parts WHERE name LIKE :query OR sku LIKE :query OR barcode LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<Part>>

    @Query("SELECT * FROM parts WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<Part>>

    @Query("UPDATE parts SET currentStock = currentStock + :adjustment, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun adjustStock(id: String, adjustment: Int): Int

    @Query("UPDATE parts SET currentStock = :quantity, updatedAt = CURRENT_TIMESTAMP, lastRestockedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun setStock(id: String, quantity: Int): Int

    @Query("UPDATE parts SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String): Int
}

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: Invoice): Long

    @Update
    suspend fun update(invoice: Invoice): Int

    @Query("DELETE FROM invoices WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    fun getById(id: String): Flow<Invoice?>

    @Query("SELECT * FROM invoices WHERE repairOrderId = :orderId")
    fun getByRepairOrderId(orderId: String): Flow<Invoice?>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getByCustomerId(customerId: String): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE createdAt BETWEEN :start AND :end ORDER BY createdAt DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE paymentStatus = :status ORDER BY createdAt DESC")
    fun getByPaymentStatus(status: String): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<Invoice>>

    @Query("UPDATE invoices SET paidAmount = paidAmount + :amount, paymentStatus = :status, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun addPayment(id: String, amount: Double, status: String): Int

    @Query("UPDATE invoices SET printedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun markPrinted(id: String): Int

    @Query("UPDATE invoices SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String): Int
}

@Dao
interface TechnicianDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(technician: Technician): Long

    @Update
    suspend fun update(technician: Technician): Int

    @Query("DELETE FROM technicians WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM technicians WHERE isActive = 1 ORDER BY name ASC")
    fun getAll(): Flow<List<Technician>>

    @Query("SELECT * FROM technicians WHERE id = :id")
    fun getById(id: String): Flow<Technician?>

    @Query("SELECT * FROM technicians WHERE userId = :userId")
    fun getByUserId(userId: String): Flow<Technician?>

    @Query("SELECT * FROM technicians WHERE isSynced = 0")
    fun getUnsynced(): Flow<List<Technician>>

    @Query("UPDATE technicians SET completedOrders = completedOrders + 1, rating = :rating, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun updateStats(id: String, rating: Double): Int

    @Query("UPDATE technicians SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String): Int
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: AppSettings): Long

    @Update
    suspend fun update(settings: AppSettings): Int

    @Query("SELECT * FROM app_settings WHERE id = 'default'")
    fun get(): Flow<AppSettings?>
}