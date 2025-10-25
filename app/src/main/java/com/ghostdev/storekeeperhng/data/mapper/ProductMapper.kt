package com.ghostdev.storekeeperhng.data.mapper

import com.ghostdev.storekeeperhng.data.local.ProductEntity
import com.ghostdev.storekeeperhng.domain.model.Product

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    productName = productName,
    quantity = quantity,
    price = price,
    imagePath = imagePath,
    createdAt = createdAt,
    updatedAt = updatedAt,
    category = category,
    description = description,
    sku = sku
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    productName = productName,
    quantity = quantity,
    price = price,
    imagePath = imagePath,
    createdAt = createdAt,
    updatedAt = updatedAt,
    category = category,
    description = description,
    sku = sku
)
