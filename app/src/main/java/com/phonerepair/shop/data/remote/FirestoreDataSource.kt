package com.phonerepair.shop.data.remote

import com.phonerepair.shop.data.model.*
import com.phonerepair.shop.data.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Mock FirestoreDataSource للاختبار المحلي بدون Firebase
 * يخزن البيانات في الذاكرة ويعمل مع Room للتخزين المحلي
 */
class FirestoreDataSource private constructor() {
    
    // In-memory storage for testing
    private val orders = mutableMapOf<String, RepairOrder>()
    private val customers = mutableMapOf<String, Customer>()
    private val parts = mutableMapOf<String, Part>()
    private val invoices = mutableMapOf<String, Invoice>()
    private val technicians = mutableMapOf<String, Technician>()
    private var settings = AppSettings()
    
    // StateFlows for reactive UI
    private val _ordersFlow = MutableStateFlow(emptyList<RepairOrder>())
    private val _customersFlow = MutableStateFlow(emptyList<Customer>())
    private val _partsFlow = MutableStateFlow(emptyList<Part>())
    private val _invoicesFlow = MutableStateFlow(emptyList<Invoice>())
    private val _techniciansFlow = MutableStateFlow(emptyList<Technician>())
    private val _settingsFlow = MutableStateFlow(settings)
    
    val allOrdersFlow: Flow<List<RepairOrder>> = _ordersFlow.asStateFlow()
    val allCustomersFlow: Flow<List<Customer>> = _customersFlow.asStateFlow()
    val allPartsFlow: Flow<List<Part>> = _partsFlow.asStateFlow()
    val allInvoicesFlow: Flow<List<Invoice>> = _invoicesFlow.asStateFlow()
    val allTechniciansFlow: Flow<List<Technician>> = _techniciansFlow.asStateFlow()
    val settingsFlow: Flow<AppSettings> = _settingsFlow.asStateFlow()
    
    companion object {
        @Volatile
        private var INSTANCE: FirestoreDataSource? = null
        
        fun getInstance(): FirestoreDataSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirestoreDataSource().also { INSTANCE = it }
            }
        }
    }
    
    init {
        // Add sample data for testing
        loadSampleData()
    }
    
    private fun loadSampleData() {
        // Sample parts
        val sampleParts = listOf(
            Part(id = "PART-001", barcode = "1234567890123", sku = "SCR-IP15PM-BLK", 
                name = "شاشة iPhone 15 Pro Max أصلية", nameAr = "شاشة آيفون 15 برو ماكس أصلية",
                category = Part.PartCategory.SCREEN, compatibleDevices = listOf("iPhone 15 Pro Max"),
                purchasePrice = 450.0, salePrice = 750.0, currentStock = 3, minStockLevel = 5, maxStockLevel = 20,
                unit = Part.Unit.PIECE, supplierName = "شركة الشاشات الذهبية", location = "رف أ-1", isActive = true),
            Part(id = "PART-002", barcode = "1234567890124", sku = "BAT-IP15-STD",
                name = "بطارية iPhone 15 أصلية", nameAr = "بطارية آيفون 15 أصلية",
                category = Part.PartCategory.BATTERY, compatibleDevices = listOf("iPhone 15", "iPhone 15 Plus"),
                purchasePrice = 85.0, salePrice = 180.0, currentStock = 12, minStockLevel = 10, maxStockLevel = 50,
                unit = Part.Unit.PIECE, supplierName = "مورد البطاريات المعتمد", location = "رف ب-3", isActive = true),
            Part(id = "PART-003", barcode = "1234567890125", sku = "CAM-S24U-MAIN",
                name = "كاميرا خلفية رئيسية S24 Ultra", nameAr = "كاميرا خلفية رئيسية اس 24 الترا",
                category = Part.PartCategory.CAMERA, compatibleDevices = listOf("Galaxy S24 Ultra"),
                purchasePrice = 220.0, salePrice = 380.0, currentStock = 0, minStockLevel = 3, maxStockLevel = 15,
                unit = Part.Unit.PIECE, supplierName = "شركة الكاميرات المتطورة", location = "رف ج-2", isActive = true)
        )
        sampleParts.forEach { parts[it.id] = it }
        _partsFlow.value = parts.values.toList()
        
        // Sample customers
        val sampleCustomers = listOf(
            Customer(id = "CUST-001", name = "أحمد محمد علي", phone = "0501234567", email = "ahmed@example.com",
                totalOrders = 12, totalSpent = 4500.0, loyaltyPoints = 750, isBlacklisted = false),
            Customer(id = "CUST-002", name = "سارة أحمد سالم", phone = "0559876543", email = "sara@example.com",
                totalOrders = 8, totalSpent = 3200.0, loyaltyPoints = 480, isBlacklisted = false),
            Customer(id = "CUST-003", name = "خالد عبدالله", phone = "0541112233", email = null,
                totalOrders = 3, totalSpent = 850.0, loyaltyPoints = 120, isBlacklisted = false)
        )
        sampleCustomers.forEach { customers[it.id] = it }
        _customersFlow.value = customers.values.toList()
        
        // Sample technicians
        val sampleTechs = listOf(
            Technician(id = "TECH-001", userId = "user-1", name = "محمد علي", phone = "0501112233",
                specialization = listOf(Technician.Specialization.SCREEN_REPLACEMENT, Technician.Specialization.BOARD_REPAIR),
                skillLevel = Technician.SkillLevel.SENIOR, isActive = true),
            Technician(id = "TECH-002", userId = "user-2", name = "فاطمة سالم", phone = "0552223344",
                specialization = listOf(Technician.Specialization.MICROSOLDERING, Technician.Specialization.DIAGNOSTICS),
                skillLevel = Technician.SkillLevel.INTERMEDIATE, isActive = true)
        )
        sampleTechs.forEach { technicians[it.id] = it }
        _techniciansFlow.value = technicians.values.toList()
    }
    
    // ============ Repair Orders ============
    suspend fun createOrder(order: RepairOrder): Result<String> {
        orders[order.id] = order
        _ordersFlow.value = orders.values.toList()
        return Result.Success(order.id)
    }
    
    suspend fun updateOrder(order: RepairOrder): Result<Unit> {
        orders[order.id] = order
        _ordersFlow.value = orders.values.toList()
        return Result.Success(Unit)
    }
    
    suspend fun deleteOrder(id: String): Result<Unit> {
        orders.remove(id)
        _ordersFlow.value = orders.values.toList()
        return Result.Success(Unit)
    }
    
    fun observeAllOrders(): Flow<List<RepairOrder>> = allOrdersFlow
    
    fun observeOrder(id: String): Flow<RepairOrder?> = allOrdersFlow.map { it.find { it.id == id } }
    
    fun observeOrdersByCustomer(customerId: String): Flow<List<RepairOrder>> = 
        allOrdersFlow.map { it.filter { it.customerId == customerId } }
    
    fun observeOrdersByTechnician(technicianId: String): Flow<List<RepairOrder>> = 
        allOrdersFlow.map { it.filter { it.technicianId == technicianId } }
    
    // ============ Customers ============
    suspend fun createCustomer(customer: Customer): Result<String> {
        customers[customer.id] = customer
        _customersFlow.value = customers.values.toList()
        return Result.Success(customer.id)
    }
    
    suspend fun updateCustomer(customer: Customer): Result<Unit> {
        customers[customer.id] = customer
        _customersFlow.value = customers.values.toList()
        return Result.Success(Unit)
    }
    
    suspend fun deleteCustomer(id: String): Result<Unit> {
        customers.remove(id)
        _customersFlow.value = customers.values.toList()
        return Result.Success(Unit)
    }
    
    fun observeAllCustomers(): Flow<List<Customer>> = allCustomersFlow
    
    fun observeCustomer(id: String): Flow<Customer?> = allCustomersFlow.map { it.find { it.id == id } }
    
    // ============ Parts ============
    suspend fun createPart(part: Part): Result<String> {
        parts[part.id] = part
        _partsFlow.value = parts.values.toList()
        return Result.Success(part.id)
    }
    
    suspend fun updatePart(part: Part): Result<Unit> {
        parts[part.id] = part
        _partsFlow.value = parts.values.toList()
        return Result.Success(Unit)
    }
    
    suspend fun deletePart(id: String): Result<Unit> {
        parts.remove(id)
        _partsFlow.value = parts.values.toList()
        return Result.Success(Unit)
    }
    
    fun observeAllParts(): Flow<List<Part>> = allPartsFlow
    
    fun observePart(id: String): Flow<Part?> = allPartsFlow.map { it.find { it.id == id } }
    
    fun observeLowStockParts(): Flow<List<Part>> = 
        allPartsFlow.map { it.filter { it.currentStock <= it.minStockLevel && it.isActive } }
    
    // ============ Invoices ============
    suspend fun createInvoice(invoice: Invoice): Result<String> {
        invoices[invoice.id] = invoice
        _invoicesFlow.value = invoices.values.toList()
        return Result.Success(invoice.id)
    }
    
    suspend fun updateInvoice(invoice: Invoice): Result<Unit> {
        invoices[invoice.id] = invoice
        _invoicesFlow.value = invoices.values.toList()
        return Result.Success(Unit)
    }
    
    suspend fun deleteInvoice(id: String): Result<Unit> {
        invoices.remove(id)
        _invoicesFlow.value = invoices.values.toList()
        return Result.Success(Unit)
    }
    
    fun observeAllInvoices(): Flow<List<Invoice>> = allInvoicesFlow
    
    fun observeInvoice(id: String): Flow<Invoice?> = allInvoicesFlow.map { it.find { it.id == id } }
    
    // ============ Technicians ============
    suspend fun createTechnician(technician: Technician): Result<String> {
        technicians[technician.id] = technician
        _techniciansFlow.value = technicians.values.toList()
        return Result.Success(technician.id)
    }
    
    suspend fun updateTechnician(technician: Technician): Result<Unit> {
        technicians[technician.id] = technician
        _techniciansFlow.value = technicians.values.toList()
        return Result.Success(Unit)
    }
    
    suspend fun deleteTechnician(id: String): Result<Unit> {
        technicians.remove(id)
        _techniciansFlow.value = technicians.values.toList()
        return Result.Success(Unit)
    }
    
    fun observeAllTechnicians(): Flow<List<Technician>> = allTechniciansFlow
    
    fun observeTechnician(id: String): Flow<Technician?> = allTechniciansFlow.map { it.find { it.id == id } }
    
    // ============ Settings ============
    suspend fun getSettings(): Result<AppSettings> = Result.Success(settings)
    
    suspend fun updateSettings(newSettings: AppSettings): Result<Unit> {
        settings = newSettings
        _settingsFlow.value = settings
        return Result.Success(Unit)
    }
    
    fun observeSettings(): Flow<AppSettings> = settingsFlow
}