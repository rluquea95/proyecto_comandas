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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EnPreparacionViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnPreparacionUiState())
    val uiState: StateFlow<EnPreparacionUiState> = _uiState

    init {
        observePedidosEnCurso()
    }

    private fun observePedidosEnCurso() {
        viewModelScope.launch {
            firestoreRepository.observePedidos().collect { todosLosPedidos ->
                val pedidosEnCurso = todosLosPedidos.filter { it.state == EstadoPedido.ENCURSO }
                _uiState.update { it.copy(pedidos = pedidosEnCurso) }
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
        val vista = _uiState.value.vista
        val seleccionadosPorVista = _uiState.value.productosSeleccionados.toMutableMap()
        val seleccionadosMesa = seleccionadosPorVista[vista]?.toMutableMap() ?: mutableMapOf()
        val claves = seleccionadosMesa[mesa]?.toMutableSet() ?: mutableSetOf()

        if (!claves.add(clave)) claves.remove(clave)
        seleccionadosMesa[mesa] = claves
        seleccionadosPorVista[vista] = seleccionadosMesa

        _uiState.value = _uiState.value.copy(productosSeleccionados = seleccionadosPorVista)
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
                    val incluir = when (_uiState.value.vista) {
                        VistaPreparacion.CAMARERO -> !unidad.preparado && !unidad.entregado
                        VistaPreparacion.COCINA -> !unidad.preparado
                    }
                    if (incluir) productoPedido to idx else null
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
        val vistaActual = _uiState.value.vista
        val productosSeleccionadosVista = _uiState.value.productosSeleccionados[vistaActual] ?: emptyMap()

        viewModelScope.launch {
            var huboError = false

            pedidosActuales.forEach { pedido ->
                val mesa = pedido.mesa
                val seleccionados = productosSeleccionadosVista[mesa.name] ?: emptySet()
                if (seleccionados.isEmpty()) return@forEach

                //Construimos la lista nueva de ProductoPedido
                val nuevaCarta = pedido.carta.map { pp ->
                    val esBebida = pp.producto.category == CategoryProducto.BEBIDA

                    //Mapeamos cada unidad: si está marcada, la pasamos a preparadas (y entregada si es camarero+bebida)
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

                //Creamos el pedido actualizado
                val pedidoActualizado = pedido.copy(carta = nuevaCarta)

                //Pedimos al repositorio que lo grabe y, si toca, marque state=LISTO
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
    val productosSeleccionados: Map<VistaPreparacion, Map<String, Set<String>>> = mapOf(
        VistaPreparacion.CAMARERO to emptyMap(),
        VistaPreparacion.COCINA to emptyMap()
    ),
    val vista: VistaPreparacion = VistaPreparacion.COCINA,
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)

enum class VistaPreparacion {
    COCINA,
    CAMARERO
}