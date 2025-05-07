package com.example.readytapas.ui.screens.tomarpedido

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.EstadoUnidad
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TomarPedidoViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TomarPedidoUiState())
    val uiState: StateFlow<TomarPedidoUiState> = _uiState

    val productosFiltrados: StateFlow<List<Producto>> = combine(
        _uiState.map { it.productos },
        _uiState.map { it.searchText },
        _uiState.map { it.categoriaSeleccionada }
    ) { productos, search, categoria ->
        productos.filter {
            (categoria == null || it.category == categoria) &&
                    it.name.contains(search, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadMesas()
        loadProductos()
    }

    // Cargar mesas libres (occupied == false)
    private fun loadMesas() {
        viewModelScope.launch {
            firestoreRepository.getMesas().onSuccess { mesas ->
                _uiState.value = _uiState.value.copy(
                    mesas = mesas.filter { !it.occupied || it.name == NumeroMesa.BARRA }
                        .sortedWith(compareBy {
                            if (it.name == NumeroMesa.BARRA) Int.MAX_VALUE else it.name.name.extractNumeroMesa()
                        })
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar mesas disponibles",
                    snackbarType = SnackbarType.ERROR
                )
                Log.e("TomarPedido", "Error al cargar mesas", it)
            }
        }
    }

    // Cargar productos de la carta
    private fun loadProductos() {
        viewModelScope.launch {
            firestoreRepository.getCarta().onSuccess { productos ->
                _uiState.value = _uiState.value.copy(productos = productos)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar productos",
                    snackbarType = SnackbarType.ERROR
                )
                Log.e("TomarPedido", "Error al cargar productos", it)
            }
        }
    }

    //Extraer el número de la mesa
    private fun String.extractNumeroMesa(): Int {
        return this.substringAfter("MESA_").toIntOrNull() ?: Int.MAX_VALUE
    }

    // Seleccionar mesa
    fun selectMesa(mesa: Mesa) {
        _uiState.value = _uiState.value.copy(mesaSeleccionada = mesa)
    }

    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
    }

    fun selectCategoria(categoria: CategoryProducto?) {
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = categoria)
    }

    // Añadir producto al pedido
    fun addProducto(producto: Producto) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val existente = productos.find { it.producto.name == producto.name }

        if (existente != null) {
            val nuevasUnidades = existente.unidades + EstadoUnidad()
            val actualizado = existente.copy(unidades = nuevasUnidades)
            productos[productos.indexOf(existente)] = actualizado
        } else {
            productos.add(
                ProductoPedido(
                    producto = producto,
                    unidades = listOf(EstadoUnidad())
                )
            )
        }
        _uiState.value = _uiState.value.copy(productosPedidos = productos)
    }

    // Aumentar cantidad
    fun increaseCantidad(productoPedido: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val index = productos.indexOfFirst { it.producto.name == productoPedido.producto.name }
        if (index != -1) {
            val actual = productos[index]
            val nuevasUnidades = actual.unidades + EstadoUnidad()
            val actualizado = actual.copy(unidades = nuevasUnidades)
            productos[index] = actualizado
            _uiState.value = _uiState.value.copy(productosPedidos = productos)
        }
    }

    // Disminuir cantidad
    fun decreaseCantidad(productoPedido: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val index = productos.indexOfFirst { it.producto.name == productoPedido.producto.name }
        if (index != -1) {
            val actual = productos[index]
            if (actual.unidades.size > 1) {
                val nuevasUnidades = actual.unidades.dropLast(1)
                productos[index] = actual.copy(unidades = nuevasUnidades)
                _uiState.value = _uiState.value.copy(productosPedidos = productos)
            } else {
                productos.removeAt(index)

                _uiState.value = _uiState.value.copy(
                    productosPedidos = productos,
                    message = "Producto eliminado: ${productoPedido.producto.name}",
                    snackbarType = SnackbarType.INFO
                )
            }
        }
    }

    // Eliminar producto del pedido
    fun removeProducto(productoPedido: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val eliminado = productos.find { it.producto.name == productoPedido.producto.name }
        productos.removeIf { it.producto.name == productoPedido.producto.name }
        _uiState.value = _uiState.value.copy(
            productosPedidos = productos,
            message = eliminado?.let { "Producto eliminado: ${it.producto.name}" },
            snackbarType = SnackbarType.INFO
        )
    }

    // Confirmar y guardar el pedido
    fun confirmPedido() {
        val mesa = _uiState.value.mesaSeleccionada
        val productos = _uiState.value.productosPedidos

        if (mesa == null || productos.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                message = "Selecciona una mesa y añade productos",
                snackbarType = SnackbarType.ERROR
            )
            return
        }

        viewModelScope.launch {
            val pedido = Pedido(
                mesa = mesa.name,
                carta = productos,
                state = EstadoPedido.ENCURSO,
                total = 0.0
            )

            val resultadoPedido = firestoreRepository.crearPedido(pedido)
            val resultadoMesa = firestoreRepository.actualizarMesa(mesa.copy(occupied = true))

            if (resultadoPedido.isSuccess && resultadoMesa.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    mesaSeleccionada = null,
                    productosPedidos = emptyList(),
                    message = "Pedido enviado a cocina ✅",
                    snackbarType = SnackbarType.SUCCESS
                )
                loadMesas()
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Error al enviar el pedido",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            snackbarType = SnackbarType.INFO
        )
    }
}

data class TomarPedidoUiState(
    val mesas: List<Mesa> = emptyList(),
    val mesaSeleccionada: Mesa? = null,
    val productos: List<Producto> = emptyList(),
    val productosPedidos: List<ProductoPedido> = emptyList(),
    val searchText: String = "",
    val categoriaSeleccionada: CategoryProducto? = null,
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)
