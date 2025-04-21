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

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = firestoreRepository.getCarta()
        }
    }

    fun seleccionarCategoria(categoria: CategoryProducto?) {
        _selectedCategoria.value = categoria
    }

    val productosFiltrados: StateFlow<List<Producto>> = combine(productos, selectedCategoria) { productos, categoria ->
        if (categoria == null) {
            productos // Mostrar todos
        } else {
            productos.filter { it.category == categoria }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
