package com.phonerepair.shop.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.phonerepair.shop.data.local.Dao
import com.phonerepair.shop.data.model.*
import com.phonerepair.shop.data.remote.FirestoreDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RepairOrderRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: RepairOrderDao
) : RepairOrderRepository {
    
    override suspend fun getAll(): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getById(id: String): Result<RepairOrder?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getById(id).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByCustomerId(customerId: String): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByCustomerId(customerId).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByTechnicianId(technicianId: String): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByTechnicianId(technicianId).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByStatus(status: RepairOrder.RepairStatus): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByStatus(status.name).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByDateRange(start: java.util.Date, end: java.util.Date): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByDateRange(start.time, end.time).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun search(query: String): Result<List<RepairOrder>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.search("%$query%").first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insert(order: RepairOrder): Result<String> = withContext(Dispatchers.IO) {
        try {
            dao.insert(order)
            firestore.createOrder(order).getOrThrow()
            Result.Success(order.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(order: RepairOrder): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(order)
            firestore.updateOrder(order).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.delete(id)
            firestore.deleteOrder(id).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observeAll(): Flow<List<RepairOrder>> = dao.getAll().distinctUntilChanged()
    
    override fun observeById(id: String): Flow<RepairOrder?> = dao.getById(id).distinctUntilChanged()
    
    override fun observeByCustomer(customerId: String): Flow<List<RepairOrder>> = dao.getByCustomerId(customerId).distinctUntilChanged()
    
    override fun observeByTechnician(technicianId: String): Flow<List<RepairOrder>> = dao.getByTechnicianId(technicianId).distinctUntilChanged()
    
    override suspend fun updateStatus(id: String, status: RepairOrder.RepairStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.updateStatus(id, status.name)
            firestore.updateOrder(
                firestore.observeOrder(id).first()!!.copy(status = status)
            ).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun assignTechnician(orderId: String, technicianId: String, technicianName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.assignTechnician(orderId, technicianId, technicianName)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun addPart(orderId: String, part: UsedPart): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val order = dao.getById(orderId).first()!!
            val updatedParts = order.partsUsed + part
            val updatedOrder = order.copy(
                partsUsed = updatedParts,
                estimatedCost = updatedParts.sumOf { it.totalPrice }
            )
            dao.update(updatedOrder)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun removePart(orderId: String, partId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val order = dao.getById(orderId).first()!!
            val updatedParts = order.partsUsed.filter { it.partId != partId }
            val updatedOrder = order.copy(
                partsUsed = updatedParts,
                estimatedCost = updatedParts.sumOf { it.totalPrice }
            )
            dao.update(updatedOrder)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun addNote(orderId: String, note: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val order = dao.getById(orderId).first()!!
            val updatedOrder = order.copy(notes = note)
            dao.update(updatedOrder)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun addImage(orderId: String, imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val order = dao.getById(orderId).first()!!
            val updatedOrder = order.copy(images = order.images + imageUrl)
            dao.update(updatedOrder)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun syncPending(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsynced = dao.getUnsynced().first()
            unsynced.forEach { order ->
                firestore.updateOrder(order).getOrThrow()
                dao.markSynced(order.id)
            }
            Result.Success(unsynced.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class CustomerRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: CustomerDao
) : CustomerRepository {
    
    override suspend fun getAll(): Result<List<Customer>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getById(id: String): Result<Customer?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getById(id).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByPhone(phone: String): Result<Customer?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByPhone(phone).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun search(query: String): Result<List<Customer>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.search("%$query%").first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insert(customer: Customer): Result<String> = withContext(Dispatchers.IO) {
        try {
            dao.insert(customer)
            firestore.createCustomer(customer).getOrThrow()
            Result.Success(customer.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(customer: Customer): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(customer)
            firestore.updateCustomer(customer).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.delete(id)
            firestore.deleteCustomer(id).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observeAll(): Flow<List<Customer>> = dao.getAll().distinctUntilChanged()
    
    override fun observeById(id: String): Flow<Customer?> = dao.getById(id).distinctUntilChanged()
    
    override suspend fun updateLoyaltyPoints(customerId: String, points: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.addLoyaltyPoints(customerId, points)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateStats(customerId: String, orderCount: Int, totalSpent: Double): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Update via increment
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun syncPending(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsynced = dao.getUnsynced().first()
            unsynced.forEach { customer ->
                firestore.updateCustomer(customer).getOrThrow()
                dao.markSynced(customer.id)
            }
            Result.Success(unsynced.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class PartRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: PartDao
) : PartRepository {
    
    override suspend fun getAll(): Result<List<Part>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getById(id: String): Result<Part?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getById(id).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByBarcode(barcode: String): Result<Part?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByBarcode(barcode).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByCategory(category: Part.PartCategory): Result<List<Part>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByCategory(category.name).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getLowStock(): Result<List<Part>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getLowStock().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun search(query: String): Result<List<Part>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.search("%$query%").first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insert(part: Part): Result<String> = withContext(Dispatchers.IO) {
        try {
            dao.insert(part)
            firestore.createPart(part).getOrThrow()
            Result.Success(part.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(part: Part): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(part)
            firestore.updatePart(part).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.delete(id)
            firestore.deletePart(id).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observeAll(): Flow<List<Part>> = dao.getAll().distinctUntilChanged()
    
    override fun observeById(id: String): Flow<Part?> = dao.getById(id).distinctUntilChanged()
    
    override fun observeLowStock(): Flow<List<Part>> = dao.getLowStock().distinctUntilChanged()
    
    override suspend fun updateStock(partId: String, quantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.setStock(partId, quantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun adjustStock(partId: String, adjustment: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.adjustStock(partId, adjustment)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun syncPending(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsynced = dao.getUnsynced().first()
            unsynced.forEach { part ->
                firestore.updatePart(part).getOrThrow()
                dao.markSynced(part.id)
            }
            Result.Success(unsynced.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class InvoiceRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: InvoiceDao
) : InvoiceRepository {
    
    override suspend fun getAll(): Result<List<Invoice>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getById(id: String): Result<Invoice?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getById(id).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByRepairOrderId(orderId: String): Result<Invoice?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByRepairOrderId(orderId).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByCustomerId(customerId: String): Result<List<Invoice>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByCustomerId(customerId).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByDateRange(start: java.util.Date, end: java.util.Date): Result<List<Invoice>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByDateRange(start.time, end.time).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByPaymentStatus(status: Invoice.PaymentStatus): Result<List<Invoice>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByPaymentStatus(status.name).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insert(invoice: Invoice): Result<String> = withContext(Dispatchers.IO) {
        try {
            dao.insert(invoice)
            firestore.createInvoice(invoice).getOrThrow()
            Result.Success(invoice.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(invoice: Invoice): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(invoice)
            firestore.updateInvoice(invoice).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.delete(id)
            firestore.deleteInvoice(id).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observeAll(): Flow<List<Invoice>> = dao.getAll().distinctUntilChanged()
    
    override fun observeById(id: String): Flow<Invoice?> = dao.getById(id).distinctUntilChanged()
    
    override suspend fun addPayment(invoiceId: String, payment: Payment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val invoice = dao.getById(invoiceId).first()!!
            val updatedPayments = invoice.payments + payment
            val newPaidAmount = invoice.paidAmount + payment.amount
            val newStatus = when {
                newPaidAmount >= invoice.totalAmount -> Invoice.PaymentStatus.PAID
                newPaidAmount > 0 -> Invoice.PaymentStatus.PARTIAL
                else -> Invoice.PaymentStatus.PENDING
            }
            val updatedInvoice = invoice.copy(
                payments = updatedPayments,
                paidAmount = newPaidAmount,
                paymentStatus = newStatus
            )
            dao.update(updatedInvoice)
            firestore.updateInvoice(updatedInvoice).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updatePaymentStatus(invoiceId: String, status: Invoice.PaymentStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val invoice = dao.getById(invoiceId).first()!!
            val updatedInvoice = invoice.copy(paymentStatus = status)
            dao.update(updatedInvoice)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun markPrinted(invoiceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.markPrinted(invoiceId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun syncPending(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsynced = dao.getUnsynced().first()
            unsynced.forEach { invoice ->
                firestore.updateInvoice(invoice).getOrThrow()
                dao.markSynced(invoice.id)
            }
            Result.Success(unsynced.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class TechnicianRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: TechnicianDao
) : TechnicianRepository {
    
    override suspend fun getAll(): Result<List<Technician>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getById(id: String): Result<Technician?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getById(id).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getByUserId(userId: String): Result<Technician?> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getByUserId(userId).first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getActive(): Result<List<Technician>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(dao.getAll().first().filter { it.isActive })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insert(technician: Technician): Result<String> = withContext(Dispatchers.IO) {
        try {
            dao.insert(technician)
            firestore.createTechnician(technician).getOrThrow()
            Result.Success(technician.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(technician: Technician): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(technician)
            firestore.updateTechnician(technician).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.delete(id)
            firestore.deleteTechnician(id).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observeAll(): Flow<List<Technician>> = dao.getAll().distinctUntilChanged()
    
    override fun observeById(id: String): Flow<Technician?> = dao.getById(id).distinctUntilChanged()
    
    override suspend fun updateStats(technicianId: String, completedOrders: Int, rating: Double): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.updateStats(technicianId, rating)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun syncPending(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val unsynced = dao.getUnsynced().first()
            unsynced.forEach { tech ->
                firestore.updateTechnician(tech).getOrThrow()
                dao.markSynced(tech.id)
            }
            Result.Success(unsynced.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class SettingsRepositoryImpl(
    private val firestore: FirestoreDataSource,
    private val dao: SettingsDao
) : SettingsRepository {
    
    override suspend fun get(): Result<AppSettings> = withContext(Dispatchers.IO) {
        try {
            val local = dao.get().first() ?: AppSettings()
            val remote = firestore.getSettings().getOrNull() ?: local
            Result.Success(remote)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun update(settings: AppSettings): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.update(settings)
            firestore.updateSettings(settings).getOrThrow()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun observe(): Flow<AppSettings> = dao.get()
        .map { it ?: AppSettings() }
        .distinctUntilChanged()
}