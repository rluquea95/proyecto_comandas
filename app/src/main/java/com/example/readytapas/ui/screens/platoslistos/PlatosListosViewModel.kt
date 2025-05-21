package com.example.readytapas.ui.screens.platoslistos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlatosListosViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlatosListosUiState())
    val uiState: StateFlow<PlatosListosUiState> = _uiState

    init {
        observePedidosEnCurso()
        //loadPedidos()
    }

    /*private fun loadPedidos() {
        viewModelScope.launch {
            firestoreRepository.getPedidos().onSuccess { pedidos ->
                val enCurso = pedidos.filter { it.state == EstadoPedido.ENCURSO }
                _uiState.value = _uiState.value.copy(pedidos = enCurso)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar pedidos",
                    snackbarType = SnackbarType.ERROR
                )
                Log.e("PlatosListos", "Error al cargar pedidos", it)
            }
        }
    }*/

    private fun observePedidosEnCurso() {
        viewModelScope.launch {
            firestoreRepository.observePedidos()
                .map { pedidos -> pedidos.filter { it.state == EstadoPedido.ENCURSO } }
                .collect { enCurso ->
                    _uiState.value = _uiState.value.copy(pedidos = enCurso)
                }
        }
    }

    fun toggleExpandido(mesa: String) {
        val actuales = _uiState.value.pedidosExpandidos.toMutableSet()
        if (!actuales.add(mesa)) actuales.remove(mesa)
        _uiState.value = _uiState.value.copy(pedidosExpandidos = actuales)
    }

    fun toggleSeleccionUnidad(mesa: String, clave: String) {
        val seleccionados = _uiState.value.productosSeleccionados.toMutableMap()
        val set = seleccionados[mesa]?.toMutableSet() ?: mutableSetOf()
        if (!set.add(clave)) set.remove(clave)
        seleccionados[mesa] = set
        _uiState.value = _uiState.value.copy(productosSeleccionados = seleccionados)
    }

    /* Los platos/tapas con preparado=true y entregado=false */
    fun getPlatosPendientesPorMesa(mesa: String): List<Pair<ProductoPedido, Int>> {
        val pedido = _uiState.value.pedidos.find { it.mesa.name == mesa } ?: return emptyList()
        return pedido.carta
            .filter { it.producto.category in listOf(CategoryProducto.PLATO, CategoryProducto.TAPA) }
            .flatMap { pp ->
                pp.unidades.mapIndexedNotNull { idx, unidad ->
                    if (unidad.preparado && !unidad.entregado) pp to idx else null
                }
            }
    }

    fun confirmEntregados() {
        val pedidos = _uiState.value.pedidos
        val seleccionados = _uiState.value.productosSeleccionados

        viewModelScope.launch {
            var huboError = false

            pedidos.forEach { pedido ->
                val mesaKey = pedido.mesa.name
                val claves = seleccionados[mesaKey] ?: emptySet()
                if (claves.isEmpty()) return@forEach

                val lista = pedido.carta.map { pp ->
                    val nuevasUnidades = pp.unidades.mapIndexed { idx, unidad ->
                        val clave = "${pp.producto.name}-$idx"
                        if (clave in claves) unidad.copy(entregado = true) else unidad
                    }
                    pp.copy(unidades = nuevasUnidades)
                }

                val actualizado = pedido.copy(carta = lista)
                val res = firestoreRepository.actualizarEstadoPedido(actualizado)

                if (res.isFailure) {
                    huboError = true
                    _uiState.value = _uiState.value.copy(
                        message = "Error al confirmar entrega de $mesaKey",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            }
            if (!huboError) {
                _uiState.value = _uiState.value.copy(
                    message = "Platos marcados como entregados",
                    snackbarType = SnackbarType.SUCCESS,
                    productosSeleccionados = emptyMap()
                )
                //loadPedidos()
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

data class PlatosListosUiState(
    val pedidos: List<Pedido> = emptyList(),
    val pedidosExpandidos: Set<String> = emptySet(),
    val productosSeleccionados: Map<String, Set<String>> = emptyMap(),
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)