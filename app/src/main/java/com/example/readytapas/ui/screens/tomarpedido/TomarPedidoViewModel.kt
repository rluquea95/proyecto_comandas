package com.example.readytapas.ui.screens.tomarpedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TomarPedidoViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    // Lista de mesas libres
    private val _mesas = MutableStateFlow<List<Mesa>>(emptyList())
    val mesas: StateFlow<List<Mesa>> = _mesas

    // Mesa seleccionada para el pedido
    private val _mesaSeleccionada = MutableStateFlow<Mesa?>(null)
    val mesaSeleccionada: StateFlow<Mesa?> = _mesaSeleccionada

    // Lista de productos de la carta
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Lista de productos que el camarero está pidiendo
    private val _productosPedidos = MutableStateFlow<List<ProductoPedido>>(emptyList())
    val productosPedidos: StateFlow<List<ProductoPedido>> = _productosPedidos

    // NUEVO - Controla si hay que mostrar el mensaje de éxito
    private val _mostrarSnackbar = MutableStateFlow(false)
    val mostrarSnackbar: StateFlow<Boolean> = _mostrarSnackbar

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _categoriaSeleccionada = MutableStateFlow<CategoryProducto?>(null)
    val categoriaSeleccionada: StateFlow<CategoryProducto?> = _categoriaSeleccionada


    init {
        cargarMesas()
        cargarProductos()
    }

    val productosFiltrados: StateFlow<List<Producto>> = combine(
        productos, searchText, categoriaSeleccionada
    ) { productos, search, categoria ->
        productos.filter {
            (categoria == null || it.category == categoria) &&
                    it.name.contains(search, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Cargar mesas libres (occupied == false)
    private fun cargarMesas() {
        viewModelScope.launch {
            _mesas.value = firestoreRepository.getMesas()
                .filter { !it.occupied || it.name == NumeroMesa.BARRA }
                .sortedWith(compareBy(
                    { if (it.name == NumeroMesa.BARRA) Int.MAX_VALUE else it.name.name.extractNumeroMesa() }
                ))
        }
    }

    // Cargar productos de la carta
    private fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = firestoreRepository.getCarta()
        }
    }

    //Extraer el número de la mesa
    private fun String.extractNumeroMesa(): Int {
        return this.substringAfter("MESA_").toIntOrNull() ?: Int.MAX_VALUE
    }

    // Seleccionar mesa
    fun seleccionarMesa(mesa: Mesa) {
        _mesaSeleccionada.value = mesa
    }

    fun actualizarTextoBusqueda(texto: String) {
        _searchText.value = texto
    }

    fun seleccionarCategoria(categoria: CategoryProducto?) {
        _categoriaSeleccionada.value = categoria
    }

    // Añadir producto al pedido
    fun agregarProducto(producto: Producto) {
        val productosActuales = _productosPedidos.value.toMutableList()
        val existente = productosActuales.find { it.producto.name == producto.name }

        if (existente != null) {
            val actualizado = existente.copy(cantidad = existente.cantidad + 1)
            productosActuales[productosActuales.indexOf(existente)] = actualizado
        } else {
            productosActuales.add(
                ProductoPedido(
                    producto = producto,
                    cantidad = 1,
                    preparado = false
                )
            )
        }
        _productosPedidos.value = productosActuales
    }

    // Aumentar cantidad
    fun aumentarCantidad(productoPedido: ProductoPedido) {
        val productosActuales = _productosPedidos.value.toMutableList()
        val index = productosActuales.indexOfFirst { it.producto.name == productoPedido.producto.name }
        if (index != -1) {
            val actualizado = productosActuales[index].copy(cantidad = productosActuales[index].cantidad + 1)
            productosActuales[index] = actualizado
            _productosPedidos.value = productosActuales
        }
    }

    // Disminuir cantidad
    fun disminuirCantidad(productoPedido: ProductoPedido) {
        val productosActuales = _productosPedidos.value.toMutableList()
        val index = productosActuales.indexOfFirst { it.producto.name == productoPedido.producto.name }
        if (index != -1) {
            val actual = productosActuales[index]
            if (actual.cantidad > 1) {
                productosActuales[index] = actual.copy(cantidad = actual.cantidad - 1)
            } else {
                productosActuales.removeAt(index)
            }
            _productosPedidos.value = productosActuales
        }
    }

    // Eliminar producto del pedido
    fun eliminarProducto(productoPedido: ProductoPedido) {
        val productosActuales = _productosPedidos.value.toMutableList()
        productosActuales.removeIf { it.producto.name == productoPedido.producto.name }
        _productosPedidos.value = productosActuales
    }

    // Confirmar y guardar el pedido
    fun confirmarPedido() {
        val mesaSeleccionada = _mesaSeleccionada.value
        val productos = _productosPedidos.value

        if (mesaSeleccionada != null && productos.isNotEmpty()) {
            viewModelScope.launch {
                val pedido = Pedido(
                    mesa = mesaSeleccionada.name,
                    carta = productos,
                    state = EstadoPedido.ENCURSO,
                    total = 0.0
                )

                firestoreRepository.crearPedido(pedido)
                firestoreRepository.actualizarMesa(mesaSeleccionada.copy(occupied = true))

                _mostrarSnackbar.value = true

                // Limpiar datos después de enviar el pedido
                _mesaSeleccionada.value = null
                _productosPedidos.value = emptyList()
                //delay(500) // Opcional: Pequeño retraso para dar tiempo a Firebase a propagar el cambio
                cargarMesas()
            }
        }
    }

    fun resetearSnackbar() {
        _mostrarSnackbar.value = false
    }
}