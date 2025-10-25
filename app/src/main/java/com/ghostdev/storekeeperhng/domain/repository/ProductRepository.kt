package com.ghostdev.storekeeperhng.domain.repository

import com.ghostdev.storekeeperhng.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProduct(id: Long): Flow<Product?>
    fun searchProducts(query: String): Flow<List<Product>>

    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Long)

    fun getTotalCount(): Flow<Int>
    fun getTotalQuantity(): Flow<Int>
    fun getTotalInventoryValue(): Flow<Double>
}