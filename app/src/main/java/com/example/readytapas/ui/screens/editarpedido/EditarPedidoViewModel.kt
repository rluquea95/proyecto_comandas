package com.example.readytapas.ui.screens.editarpedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.EstadoUnidad
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.data.repository.AuthRepository
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarPedidoViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPedidoUiState())
    val uiState: StateFlow<EditarPedidoUiState> = _uiState

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
        loadProductos()
        loadMesasConPedidosActivos()
    }

    private fun loadProductos() {
        viewModelScope.launch {
            firestoreRepository.getCarta().onSuccess { productos ->
                _uiState.value = _uiState.value.copy(productos = productos)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar productos",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    private fun loadMesasConPedidosActivos() {
        viewModelScope.launch {
            firestoreRepository.getPedidos().onSuccess { pedidos ->
                val activos = pedidos.filter {
                    it.state == EstadoPedido.ENCURSO || it.state == EstadoPedido.LISTO
                }
                val mesas = activos.map { Mesa(it.mesa, occupied = true) }
                _uiState.value = _uiState.value.copy(
                    pedidosActivos = activos,
                    mesas = mesas
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar pedidos",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    private var pedidoJob: Job? = null

    fun selectMesa(mesa: Mesa) {
        val uid = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            //Intentar lock
            val ok = firestoreRepository.lockPedido(mesa.name.name, uid)
            if (!ok) {
                // otro usuario editing
                _uiState.value = _uiState.value.copy(
                    pedidoBloqueado = true,
                    message = "Este pedido ya lo está editando otro usuario",
                    snackbarType = SnackbarType.ERROR
                )
                loadMesasConPedidosActivos()
                return@launch
            }

            //reset estado de bloqueo + seleccion
            _uiState.value = _uiState.value.copy(
                pedidoBloqueado  = false,
                mesaSeleccionada = mesa,
                message = null
            )

            //cancelar escucha previa
            pedidoJob?.cancel()

            //arrancar listener en tiempo real
            pedidoJob = viewModelScope.launch {
                firestoreRepository
                    .observePedido(mesa.name.name)
                    .collectLatest { remoto ->
                        _uiState.update { estado ->
                            estado.copy(
                                pedidoOriginal   = remoto,
                                productosPedidos = remoto.carta
                            )
                        }
                    }
            }
        }
    }


    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
    }

    fun selectCategoria(categoria: CategoryProducto?) {
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = categoria)
    }

    fun addProducto(producto: Producto) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val existente = productos.find { it.producto.name == producto.name }

        if (existente != null) {
            val nuevasUnidades = existente.unidades + EstadoUnidad()
            val actualizado = existente.copy(unidades = nuevasUnidades)
            productos[productos.indexOf(existente)] = actualizado
        } else {
            productos.add(ProductoPedido(producto, listOf(EstadoUnidad())))
        }

        _uiState.value = _uiState.value.copy(productosPedidos = productos)
    }

    fun increaseCantidad(pp: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val index = productos.indexOfFirst { it.producto.name == pp.producto.name }
        if (index != -1) {
            val nuevasUnidades = productos[index].unidades + EstadoUnidad()
            productos[index] = productos[index].copy(unidades = nuevasUnidades)
            _uiState.value = _uiState.value.copy(productosPedidos = productos)
        }
    }

    fun decreaseCantidad(pp: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        val index = productos.indexOfFirst { it.producto.name == pp.producto.name }
        if (index != -1) {
            val actual = productos[index]
            if (actual.unidades.size > 1) {
                productos[index] = actual.copy(unidades = actual.unidades.dropLast(1))
            } else {
                productos.removeAt(index)
            }
            _uiState.value = _uiState.value.copy(productosPedidos = productos)
        }
    }

    fun removeProducto(pp: ProductoPedido) {
        val productos = _uiState.value.productosPedidos.toMutableList()
        productos.removeIf { it.producto.name == pp.producto.name }
        _uiState.value = _uiState.value.copy(
            productosPedidos = productos,
            message = "Producto eliminado: ${pp.producto.name}",
            snackbarType = SnackbarType.INFO
        )
    }

    fun confirmEdicion() {
        val pedidoBase = _uiState.value.pedidoOriginal ?: return
        val productos = _uiState.value.productosPedidos
        if (productos.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                message = "No hay productos en el pedido",
                snackbarType = SnackbarType.ERROR
            )
            return
        }

        viewModelScope.launch {
            // Detectar si se han añadido unidades nuevas
            val haAumentado = productos.any { ppEditado ->
                val original = pedidoBase.carta.find { it.producto.name == ppEditado.producto.name }
                val cantidadOriginal = original?.unidades?.size ?: 0
                ppEditado.unidades.size > cantidadOriginal
            }

            val nuevoEstado = if (pedidoBase.state == EstadoPedido.LISTO && haAumentado) {
                EstadoPedido.ENCURSO
            } else {
                pedidoBase.state
            }

            val pedidoActualizado = pedidoBase.copy(
                carta = productos,
                state = nuevoEstado
            )

            val result = firestoreRepository.actualizarEstadoPedido(pedidoActualizado)

            if (result.isSuccess) {
                // Liberar bloqueo
                val uid = authRepository.currentUser?.uid ?: return@launch
                firestoreRepository.unlockPedido(pedidoActualizado.mesa.name, uid)
                // Refrescar listado
                _uiState.value = _uiState.value.copy(
                    mesaSeleccionada = null,
                    pedidoOriginal = null,
                    productosPedidos = emptyList(),
                    message = "Pedido actualizado",
                    snackbarType = SnackbarType.SUCCESS
                )
                loadMesasConPedidosActivos() // Refrescar listado
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Error al actualizar el pedido",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun eliminarPedido() {
        val pedido = _uiState.value.pedidoOriginal ?: return
        viewModelScope.launch {
            val resultado = firestoreRepository.eliminarPedidoYLiberarMesa(pedido)
            if (resultado.isSuccess) {
                // Liberar bloqueo
                val uid = authRepository.currentUser?.uid ?: return@launch
                firestoreRepository.unlockPedido(pedido.mesa.name, uid)
                // Refrescar listado
                _uiState.value = _uiState.value.copy(
                    mesaSeleccionada = null,
                    pedidoOriginal = null,
                    productosPedidos = emptyList(),
                    message = "Pedido eliminado y mesa liberada",
                    snackbarType = SnackbarType.SUCCESS
                )
                loadMesasConPedidosActivos()
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Error al eliminar el pedido",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun clearLockAndCancel() {
        pedidoJob?.cancel()
        val mesaName = _uiState.value.mesaSeleccionada?.name?.name ?: return
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            firestoreRepository.unlockPedido(mesaName, uid)
        }
    }
}

data class EditarPedidoUiState(
    val mesas: List<Mesa> = emptyList(),
    val mesaSeleccionada: Mesa? = null,
    val pedidosActivos: List<Pedido> = emptyList(),
    val pedidoOriginal: Pedido? = null,
    val productos: List<Producto> = emptyList(),
    val productosPedidos: List<ProductoPedido> = emptyList(),
    val searchText: String = "",
    val categoriaSeleccionada: CategoryProducto? = null,
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO,
    val pedidoBloqueado: Boolean = false
)

