package com.ghostdev.storekeeperhng.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.storekeeperhng.domain.model.Product
import com.ghostdev.storekeeperhng.domain.usecase.DeleteProductUseCase
import com.ghostdev.storekeeperhng.domain.usecase.GetProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val error: String? = null,
    val deleted: Boolean = false
)

class DetailViewModel(
    private val productId: Long,
    private val getProduct: GetProductUseCase,
    private val deleteProduct: DeleteProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getProduct(productId).collect { p ->
                _uiState.update { it.copy(isLoading = false, product = p) }
            }
        }
    }

    fun onDelete() {
        val id = _uiState.value.product?.id ?: return
        viewModelScope.launch {
            deleteProduct(id)
            _uiState.update { it.copy(deleted = true) }
        }
    }
}