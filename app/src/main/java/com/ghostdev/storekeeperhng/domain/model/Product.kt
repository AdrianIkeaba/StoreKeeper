package com.ghostdev.storekeeperhng.domain.model

data class Product(
    val id: Long = 0L,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imagePath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val category: String?,
    val description: String?,
    val sku: String?
) {
    val totalValue: Double get() = quantity * price
}