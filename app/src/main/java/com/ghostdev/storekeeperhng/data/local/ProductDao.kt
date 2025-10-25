package com.ghostdev.storekeeperhng.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE LOWER(productName) LIKE '%' || LOWER(:query) || '%' ORDER BY updatedAt DESC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM products")
    fun getTotalQuantity(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity * price), 0) FROM products")
    fun getTotalInventoryValue(): Flow<Double>
}