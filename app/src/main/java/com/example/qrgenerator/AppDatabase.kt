package com.example.qrgenerator

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Barcode::class, Note::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getBarcodeDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "barcodes"
                ).build()
                INSTANCE = instance

                instance
            }
        }

        fun getNoteDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}