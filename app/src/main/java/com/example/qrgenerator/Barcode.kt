package com.example.qrgenerator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barcodes")
data class Barcode(
    @PrimaryKey var id: Long,
    @ColumnInfo("date") val date: String,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("result") val result: String
) {
    constructor(data: Map<String, Any>) : this(
        data["id"] as Long,
        data["date"] as String,
        data["type"] as String,
        data["result"] as String
    )
}

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo("data") val data: String
)

