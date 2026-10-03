package com.phonerepair.shop.di

import android.content.Context
import androidx.room.Room
import com.phonerepair.shop.data.local.AppDatabase
import com.phonerepair.shop.data.local.Dao
import com.phonerepair.shop.data.remote.FirestoreDataSource
import com.phonerepair.shop.data.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "phone_repair_db"
        ).fallbackToDestructiveMigration().build()
    }
    
    @Provides
    fun provideRepairOrderDao(database: AppDatabase): RepairOrderDao = database.repairOrderDao()
    
    @Provides
    fun provideCustomerDao(database: AppDatabase): CustomerDao = database.customerDao()
    
    @Provides
    fun providePartDao(database: AppDatabase): PartDao = database.partDao()
    
    @Provides
    fun provideInvoiceDao(database: AppDatabase): InvoiceDao = database.invoiceDao()
    
    @Provides
    fun provideTechnicianDao(database: AppDatabase): TechnicianDao = database.technicianDao()
    
    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao = database.settingsDao()
}

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {
    @Provides
    @Singleton
    fun provideFirestoreDataSource(): FirestoreDataSource = FirestoreDataSource.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRepairOrderRepository(
        firestore: FirestoreDataSource,
        dao: RepairOrderDao
    ): RepairOrderRepository = RepairOrderRepositoryImpl(firestore, dao)
    
    @Provides
    @Singleton
    fun provideCustomerRepository(
        firestore: FirestoreDataSource,
        dao: CustomerDao
    ): CustomerRepository = CustomerRepositoryImpl(firestore, dao)
    
    @Provides
    @Singleton
    fun providePartRepository(
        firestore: FirestoreDataSource,
        dao: PartDao
    ): PartRepository = PartRepositoryImpl(firestore, dao)
    
    @Provides
    @Singleton
    fun provideInvoiceRepository(
        firestore: FirestoreDataSource,
        dao: InvoiceDao
    ): InvoiceRepository = InvoiceRepositoryImpl(firestore, dao)
    
    @Provides
    @Singleton
    fun provideTechnicianRepository(
        firestore: FirestoreDataSource,
        dao: TechnicianDao
    ): TechnicianRepository = TechnicianRepositoryImpl(firestore, dao)
    
    @Provides
    @Singleton
    fun provideSettingsRepository(
        firestore: FirestoreDataSource,
        dao: SettingsDao
    ): SettingsRepository = SettingsRepositoryImpl(firestore, dao)
}