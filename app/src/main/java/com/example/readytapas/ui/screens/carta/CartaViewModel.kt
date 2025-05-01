package com.example.readytapas.ui.screens.carta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartaViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartaUiState())
    val uiState: StateFlow<CartaUiState> = _uiState

    val productosFiltrados: StateFlow<List<Producto>> = combine(
        _uiState.map { it.productos },
        _uiState.map { it.categoriaSeleccionada },
        _uiState.map { it.ordenarPorPrecio },
        _uiState.map { it.searchText }
    ) {
      productos, categoria, ordenar, search ->
        var filtrados = if (categoria == null) productos else productos.filter { it.category == categoria }

        if (search.isNotBlank()) {
            filtrados = filtrados.filter { it.name.contains(search, ignoreCase = true) }
        }

        if (ordenar) filtrados.sortedBy { it.price } else filtrados
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProductos()
    }

    private fun loadProductos() {
        viewModelScope.launch {
            val result = firestoreRepository.getCarta()
            result.onSuccess { productos ->
                _uiState.value = _uiState.value.copy(productos = productos)
            }.onFailure {
                // Aquí podrías añadir un mensaje de error en el estado si quieres mostrarlo
                Log.e("CartaViewModel", "Error al cargar productos", it)
            }
        }
    }

    fun selectCategoria(categoria: CategoryProducto?) {
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = categoria)
    }

    fun selectOrdenPrecio() {
        _uiState.value = _uiState.value.copy(ordenarPorPrecio = !_uiState.value.ordenarPorPrecio)
    }

    fun updateSearchText(newText: String) {
        _uiState.value = _uiState.value.copy(searchText = newText)
    }

    fun selectProducto(producto: Producto) {
        _uiState.value = _uiState.value.copy(productoSeleccionado = producto)
    }

    fun clearSelectedProducto() {
        _uiState.value = _uiState.value.copy(productoSeleccionado = null)
    }
}

data class CartaUiState(
    val productos: List<Producto> = emptyList(),
    val categoriaSeleccionada: CategoryProducto? = null,
    val ordenarPorPrecio: Boolean = false,
    val searchText: String = "",
    val productoSeleccionado: Producto? = null
)
