package com.ghostdev.storekeeperhng.domain.usecase

import com.ghostdev.storekeeperhng.domain.model.Product
import com.ghostdev.storekeeperhng.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class AddProductUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(product: Product): Long {
        require(product.productName.isNotBlank()) { "Product name is required" }
        require(product.quantity >= 0) { "Quantity must be >= 0" }
        require(product.price > 0.0) { "Price must be greater than 0" }
        return repo.addProduct(product.copy(
            createdAt = if (product.id == 0L) System.currentTimeMillis() else product.createdAt,
            updatedAt = System.currentTimeMillis()
        ))
    }
}

class GetProductsUseCase(private val repo: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> = repo.getProducts()
}

class GetProductUseCase(private val repo: ProductRepository) {
    operator fun invoke(id: Long): Flow<Product?> = repo.getProduct(id)
}

class UpdateProductUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(product: Product) {
        require(product.id != 0L) { "Invalid product id" }
        require(product.productName.isNotBlank()) { "Product name is required" }
        require(product.quantity >= 0) { "Quantity must be >= 0" }
        require(product.price > 0.0) { "Price must be greater than 0" }
        repo.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
    }
}

class DeleteProductUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(id: Long) = repo.deleteProduct(id)
}

class SearchProductsUseCase(private val repo: ProductRepository) {
    operator fun invoke(query: String): Flow<List<Product>> = repo.searchProducts(query)
}

data class Totals(
    val count: Int,
    val quantity: Int,
    val value: Double
)

class GetTotalsUseCase(private val repo: ProductRepository) {
    data class Flows(
        val count: Flow<Int>,
        val quantity: Flow<Int>,
        val value: Flow<Double>
    )
    fun flows(): Flows = Flows(
        count = repo.getTotalCount(),
        quantity = repo.getTotalQuantity(),
        value = repo.getTotalInventoryValue()
    )
}