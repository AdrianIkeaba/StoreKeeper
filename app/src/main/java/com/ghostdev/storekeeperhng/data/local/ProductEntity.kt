package com.ghostdev.storekeeperhng.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["productName"], unique = false)]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imagePath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val category: String?,
    val description: String?,
    val sku: String?
)