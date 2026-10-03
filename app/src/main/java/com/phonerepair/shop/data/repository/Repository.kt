package com.phonerepair.shop.data.repository

import com.phonerepair.shop.data.model.*
import kotlinx.coroutines.flow.Flow

interface RepairOrderRepository {
    suspend fun getAll(): Result<List<RepairOrder>>
    suspend fun getById(id: String): Result<RepairOrder?>
    suspend fun getByCustomerId(customerId: String): Result<List<RepairOrder>>
    suspend fun getByTechnicianId(technicianId: String): Result<List<RepairOrder>>
    suspend fun getByStatus(status: RepairOrder.RepairStatus): Result<List<RepairOrder>>
    suspend fun getByDateRange(start: Date, end: Date): Result<List<RepairOrder>>
    suspend fun search(query: String): Result<List<RepairOrder>>
    suspend fun insert(order: RepairOrder): Result<String>
    suspend fun update(order: RepairOrder): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    fun observeAll(): Flow<List<RepairOrder>>
    fun observeById(id: String): Flow<RepairOrder?>
    fun observeByCustomer(customerId: String): Flow<List<RepairOrder>>
    fun observeByTechnician(technicianId: String): Flow<List<RepairOrder>>
    suspend fun updateStatus(id: String, status: RepairOrder.RepairStatus): Result<Unit>
    suspend fun assignTechnician(orderId: String, technicianId: String, technicianName: String): Result<Unit>
    suspend fun addPart(orderId: String, part: UsedPart): Result<Unit>
    suspend fun removePart(orderId: String, partId: String): Result<Unit>
    suspend fun addNote(orderId: String, note: String): Result<Unit>
    suspend fun addImage(orderId: String, imageUrl: String): Result<Unit>
    suspend fun syncPending(): Result<Int>
}

interface CustomerRepository {
    suspend fun getAll(): Result<List<Customer>>
    suspend fun getById(id: String): Result<Customer?>
    suspend fun getByPhone(phone: String): Result<Customer?>
    suspend fun search(query: String): Result<List<Customer>>
    suspend fun insert(customer: Customer): Result<String>
    suspend fun update(customer: Customer): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    fun observeAll(): Flow<List<Customer>>
    fun observeById(id: String): Flow<Customer?>
    suspend fun updateLoyaltyPoints(customerId: String, points: Int): Result<Unit>
    suspend fun updateStats(customerId: String, orderCount: Int, totalSpent: Double): Result<Unit>
    suspend fun syncPending(): Result<Int>
}

interface PartRepository {
    suspend fun getAll(): Result<List<Part>>
    suspend fun getById(id: String): Result<Part?>
    suspend fun getByBarcode(barcode: String): Result<Part?>
    suspend fun getByCategory(category: Part.PartCategory): Result<List<Part>>
    suspend fun getLowStock(): Result<List<Part>>
    suspend fun search(query: String): Result<List<Part>>
    suspend fun insert(part: Part): Result<String>
    suspend fun update(part: Part): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    fun observeAll(): Flow<List<Part>>
    fun observeById(id: String): Flow<Part?>
    fun observeLowStock(): Flow<List<Part>>
    suspend fun updateStock(partId: String, quantity: Int): Result<Unit>
    suspend fun adjustStock(partId: String, adjustment: Int): Result<Unit>
    suspend fun syncPending(): Result<Int>
}

interface InvoiceRepository {
    suspend fun getAll(): Result<List<Invoice>>
    suspend fun getById(id: String): Result<Invoice?>
    suspend fun getByRepairOrderId(orderId: String): Result<Invoice?>
    suspend fun getByCustomerId(customerId: String): Result<List<Invoice>>
    suspend fun getByDateRange(start: Date, end: Date): Result<List<Invoice>>
    suspend fun getByPaymentStatus(status: Invoice.PaymentStatus): Result<List<Invoice>>
    suspend fun insert(invoice: Invoice): Result<String>
    suspend fun update(invoice: Invoice): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    fun observeAll(): Flow<List<Invoice>>
    fun observeById(id: String): Flow<Invoice?>
    suspend fun addPayment(invoiceId: String, payment: Payment): Result<Unit>
    suspend fun updatePaymentStatus(invoiceId: String, status: Invoice.PaymentStatus): Result<Unit>
    suspend fun markPrinted(invoiceId: String): Result<Unit>
    suspend fun syncPending(): Result<Int>
}

interface TechnicianRepository {
    suspend fun getAll(): Result<List<Technician>>
    suspend fun getById(id: String): Result<Technician?>
    suspend fun getByUserId(userId: String): Result<Technician?>
    suspend fun getActive(): Result<List<Technician>>
    suspend fun insert(technician: Technician): Result<String>
    suspend fun update(technician: Technician): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    fun observeAll(): Flow<List<Technician>>
    fun observeById(id: String): Flow<Technician?>
    suspend fun updateStats(technicianId: String, completedOrders: Int, rating: Double): Result<Unit>
    suspend fun syncPending(): Result<Int>
}

interface SettingsRepository {
    suspend fun get(): Result<AppSettings>
    suspend fun update(settings: AppSettings): Result<Unit>
    fun observe(): Flow<AppSettings>
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}