package com.example.readytapas.ui.screens.enpreparacion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.*
import com.example.readytapas.data.repository.FirestoreRepository
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
                    isError = true
                )
                Log.e("EnPreparacion", "Error al cargar pedidos", it)
            }
        }
    }

    fun toggleExpandido(mesa: String) {
        val actuales = _uiState.value.pedidosExpandidos.toMutableSet()
        if (!actuales.add(mesa)) {
            actuales.remove(mesa)
        }
        _uiState.value = _uiState.value.copy(pedidosExpandidos = actuales)
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
                when (_uiState.value.vista) {
                    VistaPreparacion.CAMARERO -> {
                        // Una línea por unidad
                        productoPedido.unidades.mapIndexedNotNull { idx, unidad ->
                            if (!unidad.preparado) productoPedido to idx else null
                        }
                    }

                    VistaPreparacion.COCINA -> {
                        productoPedido.unidades.mapIndexedNotNull { idx, unidad ->
                            if (!unidad.preparado) productoPedido to idx else null
                        }
                    }
                }
            }
    }

    fun changeVista(nuevaVista: VistaPreparacion) {
        _uiState.value = _uiState.value.copy(vista = nuevaVista)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun confirmPreparados() {
        val pedidosActuales = _uiState.value.pedidos
        val productosSeleccionados = _uiState.value.productosSeleccionados

        viewModelScope.launch {
            var huboError = false

            for (pedido in pedidosActuales) {
                val mesa = pedido.mesa
                val productos = pedido.carta.toMutableList()
                val seleccionados = productosSeleccionados[mesa.name] ?: emptySet()

                if (seleccionados.isNotEmpty()) {
                    for (i in productos.indices) {
                        val producto = productos[i]

                        val nuevasUnidades = producto.unidades.mapIndexed { idx, unidad ->
                            val clave = "${producto.producto.name}-$idx"
                            if (clave in seleccionados) unidad.copy(preparado = true) else unidad
                        }

                        productos[i] = producto.copy(unidades = nuevasUnidades)
                    }

                    val pedidoActualizado = pedido.copy(carta = productos)

                    val result = firestoreRepository.actualizarPedidoPorMesa(pedidoActualizado)

                    if (result.isFailure) {
                        _uiState.value = _uiState.value.copy(
                            message = "Error al actualizar pedido de ${mesa.name}",
                            isError = true
                        )
                        huboError = true
                    }
                }
            }

            if (!huboError) {
                _uiState.value = _uiState.value.copy(
                    message = "Productos marcados como preparados ✅",
                    isError = false,
                    productosSeleccionados = emptyMap()
                )
                loadPedidos()
            }
        }
    }
}


data class EnPreparacionUiState(
    val pedidos: List<Pedido> = emptyList(),
    val pedidosExpandidos: Set<String> = emptySet(),
    val productosSeleccionados: Map<String, Set<String>> = emptyMap(), // mesa -> claves seleccionadas
    val vista: VistaPreparacion = VistaPreparacion.COCINA,
    val message: String? = null,
    val isError: Boolean = false
)

enum class VistaPreparacion {
    COCINA,
    CAMARERO
}