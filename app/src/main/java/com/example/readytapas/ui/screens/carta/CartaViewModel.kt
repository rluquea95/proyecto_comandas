package com.example.readytapas.ui.screens.carta

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

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _selectedCategoria = MutableStateFlow<CategoryProducto?>(null) // null == TODOS
    val selectedCategoria: StateFlow<CategoryProducto?> = _selectedCategoria

    private val _ordenarPorPrecio = MutableStateFlow(false)
    val ordenarPorPrecio: StateFlow<Boolean> = _ordenarPorPrecio

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _imagenProductoSeleccionada = MutableStateFlow<Producto?>(null)
    val imagenProductoSeleccionada: StateFlow<Producto?> = _imagenProductoSeleccionada

    init {
        cargarProductos()
    }

    val productosFiltrados: StateFlow<List<Producto>> = combine(
        productos, selectedCategoria, ordenarPorPrecio, searchText
    ) { productos, categoria, ordenar, search ->
        var filtrados = if (categoria == null) {
            productos // Mostrar todos
        } else {
            productos.filter { it.category == categoria }
        }
        if (search.isNotBlank()) {
            filtrados = filtrados.filter {
                it.name.contains(search, ignoreCase = true)
            }
        }
        if (ordenar){
            filtrados.sortedBy { it.price }
        }
        else{
            filtrados
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = firestoreRepository.getCarta()
        }
    }

    fun seleccionarCategoria(categoria: CategoryProducto?) {
        _selectedCategoria.value = categoria
    }

    fun alternarOrdenPrecio() {
        _ordenarPorPrecio.value = !_ordenarPorPrecio.value
    }

    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _searchText.value = nuevoTexto
    }

    fun seleccionarImagenProducto(producto: Producto) {
        _imagenProductoSeleccionada.value = producto
    }

    fun cerrarImagenProducto() {
        _imagenProductoSeleccionada.value = null
    }
}
