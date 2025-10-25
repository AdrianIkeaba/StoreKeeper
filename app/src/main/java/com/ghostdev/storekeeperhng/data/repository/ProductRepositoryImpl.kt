package com.ghostdev.storekeeperhng.data.repository

import com.ghostdev.storekeeperhng.data.local.ProductDao
import com.ghostdev.storekeeperhng.data.mapper.toDomain
import com.ghostdev.storekeeperhng.data.mapper.toEntity
import com.ghostdev.storekeeperhng.domain.model.Product
import com.ghostdev.storekeeperhng.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val dao: ProductDao
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = dao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    override fun getProduct(id: Long): Flow<Product?> = dao.getProductById(id).map { it?.toDomain() }

    override fun searchProducts(query: String): Flow<List<Product>> = dao.searchProducts(query).map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun addProduct(product: Product): Long = dao.insert(product.toEntity())

    override suspend fun updateProduct(product: Product) {
        dao.update(product.toEntity())
    }

    override suspend fun deleteProduct(id: Long) {
        dao.deleteById(id)
    }

    override fun getTotalCount(): Flow<Int> = dao.getTotalCount()

    override fun getTotalQuantity(): Flow<Int> = dao.getTotalQuantity()

    override fun getTotalInventoryValue(): Flow<Double> = dao.getTotalInventoryValue()
}