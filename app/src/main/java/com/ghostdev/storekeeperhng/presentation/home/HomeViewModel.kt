package com.ghostdev.storekeeperhng.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostdev.storekeeperhng.domain.model.Product
import com.ghostdev.storekeeperhng.domain.usecase.GetProductsUseCase
import com.ghostdev.storekeeperhng.domain.usecase.GetTotalsUseCase
import com.ghostdev.storekeeperhng.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest as flatMapLatestFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val query: String = "",
    val totalCount: Int = 0,
    val totalQuantity: Int = 0,
    val totalValue: Double = 0.0,
    val error: String? = null
)

class HomeViewModel(
    private val getProducts: GetProductsUseCase,
    private val searchProducts: SearchProductsUseCase,
    private val getTotals: GetTotalsUseCase,
    private val deleteProductUseCase: com.ghostdev.storekeeperhng.domain.usecase.DeleteProductUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Triggers a short grace loading period whenever query changes (including app start)
    private val loadingTrigger = MutableStateFlow(System.currentTimeMillis())

    // Emits true immediately, then false after a short delay, and resets on each trigger change
    private val minLoadingActive = loadingTrigger
        .flatMapLatestFlow {
            flow {
                emit(true)
                delay(650)
                emit(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    @OptIn(FlowPreview::class)
    private val productsFlow = _query
        .debounce(250)
        .flatMapLatest { q ->
            if (q.isBlank()) getProducts() else searchProducts(q)
        }

    private val totalsFlows = getTotals.flows()

    val uiState: StateFlow<HomeUiState> = combine(
        productsFlow,
        totalsFlows.count,
        totalsFlows.quantity,
        totalsFlows.value,
        minLoadingActive
    ) { products, count, qty, value, minActive ->
        HomeUiState(
            // Keep spinner while in the grace period if list is still empty.
            isLoading = products.isEmpty() && minActive,
            products = products,
            query = _query.value,
            totalCount = count,
            totalQuantity = qty,
            totalValue = value,
            error = null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onQueryChange(new: String) {
        viewModelScope.launch {
            _query.emit(new)
            // Reset loading grace period on every query change
            loadingTrigger.emit(System.currentTimeMillis())
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch { deleteProductUseCase(id) }
    }

    init {
        // Ensure the initial grace period runs on first launch
        viewModelScope.launch {
            loadingTrigger.emit(System.currentTimeMillis())
        }
    }
}