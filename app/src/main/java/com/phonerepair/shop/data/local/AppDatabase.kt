package com.phonerepair.shop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phonerepair.shop.data.model.*

@Database(
    entities = [
        RepairOrder::class,
        Customer::class,
        Part::class,
        Invoice::class,
        Technician::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repairOrderDao(): RepairOrderDao
    abstract fun customerDao(): CustomerDao
    abstract fun partDao(): PartDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "phone_repair_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}