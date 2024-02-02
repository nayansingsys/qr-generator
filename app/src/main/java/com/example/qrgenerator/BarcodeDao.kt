package com.example.qrgenerator

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarcodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBarcode(vararg barcode: Barcode)

    @Query("SELECT * FROM barcodes")
    suspend fun getAllBarcodes(): List<Barcode>

    @Delete
    fun deleteBarcode(vararg barcode: Barcode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(vararg note: Note)

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
}