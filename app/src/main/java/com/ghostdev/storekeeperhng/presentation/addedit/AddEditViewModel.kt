package com.ghostdev.storekeeperhng.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.storekeeperhng.domain.model.Product
import com.ghostdev.storekeeperhng.domain.usecase.AddProductUseCase
import com.ghostdev.storekeeperhng.domain.usecase.GetProductUseCase
import com.ghostdev.storekeeperhng.domain.usecase.UpdateProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FieldError(val message: String)

data class AddEditUiState(
    val id: Long? = null,
    val name: String = "",
    val quantityText: String = "",
    val price: String = "",
    val sku: String = "",
    val category: String = "",
    val description: String = "",
    val imagePath: String? = null,
    val imageCacheBust: Long = 0L,
    val nameError: FieldError? = null,
    val quantityError: FieldError? = null,
    val priceError: FieldError? = null,
    val isSaving: Boolean = false,
    val isValid: Boolean = false,
    val isEditMode: Boolean = false,
    val saveSuccess: Boolean = false,
)

class AddEditViewModel(
    private val productId: Long?,
    private val getProduct: GetProductUseCase,
    private val addProduct: AddProductUseCase,
    private val updateProduct: UpdateProductUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState(isEditMode = productId != null))
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (productId != null && productId != 0L) {
            viewModelScope.launch {
                getProduct(productId).collect { p ->
                    if (p != null) {
                        _uiState.update {
                            it.copy(
                                id = p.id,
                                name = p.productName,
                                quantityText = p.quantity.toString(),
                                price = if (p.price == 0.0) "" else p.price.toString(),
                                sku = p.sku ?: "",
                                category = p.category ?: "",
                                description = p.description ?: "",
                                imagePath = p.imagePath,
                                isEditMode = true
                            )
                        }
                        validate()
                    }
                }
            }
        } else {
            validate()
        }
    }

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v) }; validate() }
    fun onQuantityChange(v: String) { _uiState.update { it.copy(quantityText = v) }; validate() }
    fun onPriceChange(v: String) { _uiState.update { it.copy(price = v) }; validate() }
    fun onSkuChange(v: String) { _uiState.update { it.copy(sku = v) } }
    fun onCategoryChange(v: String) { _uiState.update { it.copy(category = v) } }
    fun onDescriptionChange(v: String) { _uiState.update { it.copy(description = v) } }
    fun onImagePathChange(v: String?) { _uiState.update { it.copy(imagePath = v, imageCacheBust = if (v.isNullOrBlank()) 0L else System.currentTimeMillis()) } }
    fun onImageRotated() { _uiState.update { it.copy(imageCacheBust = System.currentTimeMillis()) } }

    private fun validate() {
        val s = _uiState.value
        val nameErr = if (s.name.isBlank()) FieldError("Product name is required") else null
        val qtyInt = s.quantityText.toIntOrNull()
        val qErr = if (s.quantityText.isBlank()) FieldError("Quantity is required") else if (qtyInt == null || qtyInt < 0) FieldError("Quantity must be >= 0") else null
        val priceDouble = s.price.toDoubleOrNull()
        val pErr = if (priceDouble == null || priceDouble <= 0.0) FieldError("Price must be greater than 0") else null
        _uiState.update { it.copy(nameError = nameErr, quantityError = qErr, priceError = pErr, isValid = nameErr == null && qErr == null && pErr == null) }
    }

    fun save() {
        val s = _uiState.value
        if (!s.isValid || _uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val qty = s.quantityText.toIntOrNull() ?: 0
            val product = Product(
                id = s.id ?: 0L,
                productName = s.name.trim(),
                quantity = qty,
                price = s.price.toDoubleOrNull() ?: 0.0,
                imagePath = s.imagePath,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                category = s.category.ifBlank { null },
                description = s.description.ifBlank { null },
                sku = s.sku.ifBlank { null }
            )
            try {
                if (s.isEditMode && product.id != 0L) {
                    updateProduct(product)
                } else {
                    val id = addProduct(product)
                    _uiState.update { it.copy(id = id) }
                }
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}