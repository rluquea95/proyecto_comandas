package com.example.readytapas.ui.screens.carta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
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

    /* Combina diferentes flujos de datos en un solo flujo de datos.
    * El flujo de datos se actualiza cada vez que uno de los flujos de datos se actualiza. */
    val productosFiltrados: StateFlow<List<Producto>> = combine(
        _uiState.map { it.productos },
        _uiState.map { it.categoriaSeleccionada },
        _uiState.map { it.ordenarPorPrecio },
        _uiState.map { it.searchText }
    ) {
      productos, categoria, ordenar, search ->
        // Filtra los productos según la categoría seleccionada
        var filtrados = if (categoria == null) productos else productos.filter { it.category == categoria }

        // Filtra los productos según el texto de búsqueda sin distinguir entre mayusculas y minusculas
        if (search.isNotBlank()) {
            filtrados = filtrados.filter { it.name.contains(search, ignoreCase = true) }
        }

        if (ordenar) filtrados.sortedBy { it.price } else filtrados

        /* Convierte el flujo de datos en un StateFlow:
        *  - Vive mientras el ViewModel esté activo (viewModelScope)
        *  - Solo permanece activo mientras hay observadores (se suspende tras 5 segundos sin uso)
        *  - El valor inicial es una lista vacía */
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProductos()
    }

    private fun loadProductos() {
        viewModelScope.launch {
            val result = firestoreRepository.getCarta()
            result.onSuccess { productos ->
                if (productos.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        message = "No se encontraron productos.",
                        snackbarType = SnackbarType.ERROR
                    )
                } else {
                    _uiState.value = _uiState.value.copy(productos = productos)
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar productos",
                    snackbarType = SnackbarType.ERROR
                )
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

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            snackbarType = SnackbarType.INFO
        )
    }
}

data class CartaUiState(
    val productos: List<Producto> = emptyList(),
    val categoriaSeleccionada: CategoryProducto? = null,
    val ordenarPorPrecio: Boolean = false,
    val searchText: String = "",
    val productoSeleccionado: Producto? = null,
    val message: String? = null,
    val isError: Boolean = false,
    val snackbarType: SnackbarType = SnackbarType.INFO
)
