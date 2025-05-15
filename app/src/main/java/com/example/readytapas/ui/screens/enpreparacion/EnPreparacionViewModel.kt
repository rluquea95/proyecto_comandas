package com.example.readytapas.ui.screens.enpreparacion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.*
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EnPreparacionViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnPreparacionUiState())
    val uiState: StateFlow<EnPreparacionUiState> = _uiState

    init {
        loadPedidos()
    }

    private fun loadPedidos() {
        viewModelScope.launch {
            firestoreRepository.getPedidos().onSuccess { pedidos ->
                val pedidosEnCurso = pedidos.filter { it.state == EstadoPedido.ENCURSO }
                _uiState.value = _uiState.value.copy(pedidos = pedidosEnCurso)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar pedidos",
                    snackbarType = SnackbarType.ERROR
                )
                Log.e("EnPreparacion", "Error al cargar pedidos", it)
            }
        }
    }

    fun toggleExpandido(mesa: String) {
        val vista = _uiState.value.vista
        val nuevosMap = _uiState.value.pedidosExpandidos.toMutableMap()
        val actuales = nuevosMap[vista]?.toMutableSet() ?: mutableSetOf()

        if (!actuales.add(mesa)) actuales.remove(mesa)
        nuevosMap[vista] = actuales

        _uiState.value = _uiState.value.copy(pedidosExpandidos = nuevosMap)
    }

    fun toggleSeleccionUnidad(mesa: String, clave: String) {
        val seleccionados = _uiState.value.productosSeleccionados.toMutableMap()
        val productosMesa = seleccionados[mesa]?.toMutableSet() ?: mutableSetOf()

        if (!productosMesa.add(clave)) {
            productosMesa.remove(clave)
        }

        seleccionados[mesa] = productosMesa
        _uiState.value = _uiState.value.copy(productosSeleccionados = seleccionados)
    }

    fun getPedidosConPendientes(): List<Pedido> {
        return _uiState.value.pedidos
            .filter { pedido ->
                pedido.carta
                    .filter {
                        when (_uiState.value.vista) {
                            VistaPreparacion.CAMARERO -> it.producto.category == CategoryProducto.BEBIDA
                            VistaPreparacion.COCINA -> it.producto.category in listOf(
                                CategoryProducto.PLATO, CategoryProducto.TAPA
                            )
                        }
                    }
                    .flatMap { it.unidades }
                    .any { !it.preparado && !it.entregado }
            }
    }

    fun getProductosPendientesPorMesa(mesa: String): List<Pair<ProductoPedido, Int>> {
        val pedido = _uiState.value.pedidos.find { it.mesa.name == mesa } ?: return emptyList()

        return pedido.carta
            .filter {
                when (_uiState.value.vista) {
                    VistaPreparacion.CAMARERO -> it.producto.category == CategoryProducto.BEBIDA
                    VistaPreparacion.COCINA -> it.producto.category in listOf(
                        CategoryProducto.PLATO, CategoryProducto.TAPA
                    )
                }
            }
            .flatMap { productoPedido ->
                productoPedido.unidades.mapIndexedNotNull { idx, unidad ->
                    if (!unidad.preparado && !unidad.entregado) productoPedido to idx else null
                }
            }
    }

    fun changeVista(nuevaVista: VistaPreparacion) {
        _uiState.value = _uiState.value.copy(vista = nuevaVista)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null
        )
    }

    fun confirmPreparados() {
        val pedidosActuales = _uiState.value.pedidos
        val productosSeleccionados = _uiState.value.productosSeleccionados
        val vistaActual = _uiState.value.vista

        viewModelScope.launch {
            var huboError = false

            pedidosActuales.forEach { pedido ->
                val mesa = pedido.mesa
                val seleccionados = productosSeleccionados[mesa.name] ?: emptySet()
                if (seleccionados.isEmpty()) return@forEach

                // 1) Construimos la lista nueva de ProductoPedido
                val nuevaCarta = pedido.carta.map { pp ->
                    val esBebida = pp.producto.category == CategoryProducto.BEBIDA

                    // Mapeamos cada unidad: si está marcada, la pasamos a preparadas (y entregada si es camarero+bebida)
                    val nuevasUnidades = pp.unidades.mapIndexed { idx, unidad ->
                        val clave = "${pp.producto.name}-$idx"
                        if (clave in seleccionados) {
                            unidad.copy(
                                preparado = true,
                                entregado = if (vistaActual == VistaPreparacion.CAMARERO && esBebida) true else unidad.entregado
                            )
                        } else unidad
                    }
                    pp.copy(unidades = nuevasUnidades)
                }

                // 2) Creamos el pedido actualizado
                val pedidoActualizado = pedido.copy(carta = nuevaCarta)

                // 3) Pedimos al repositorio que lo grabe y, si toca, marque state=LISTO
                val result = firestoreRepository.actualizarEstadoPedido(pedidoActualizado)
                if (result.isFailure) {
                    huboError = true
                    _uiState.value = _uiState.value.copy(
                        message = "Error al actualizar pedido de ${mesa.name}",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            }

            if (!huboError) {
                _uiState.value = _uiState.value.copy(
                    message = "Productos marcados como preparados",
                    snackbarType = SnackbarType.SUCCESS,
                    productosSeleccionados = emptyMap()
                )
                loadPedidos()
            }
        }
    }
}

data class EnPreparacionUiState(
    val pedidos: List<Pedido> = emptyList(),
    val pedidosExpandidos: Map<VistaPreparacion, Set<String>> = mapOf(
        VistaPreparacion.CAMARERO to emptySet(),
        VistaPreparacion.COCINA to emptySet()
    ),
    val productosSeleccionados: Map<String, Set<String>> = emptyMap(), // mesa -> claves seleccionadas
    val vista: VistaPreparacion = VistaPreparacion.COCINA,
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)

enum class VistaPreparacion {
    COCINA,
    CAMARERO
}