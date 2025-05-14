package com.example.readytapas.ui.screens.pendientecobro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendienteCobroViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendienteCobroUiState())
    val uiState: StateFlow<PendienteCobroUiState> = _uiState

    // Canal para notificar “este pedido ya está cobrado”
    private val _cobradoEvent = Channel<Pedido>(Channel.BUFFERED)
    val cobradoEvent = _cobradoEvent.receiveAsFlow()

    init {
        loadPedidosListos()
    }

    private fun loadPedidosListos() {
        viewModelScope.launch {
            firestoreRepository.getPedidos().onSuccess { pedidos ->
                val listos = pedidos.filter { it.state == EstadoPedido.LISTO }
                _uiState.value = _uiState.value.copy(pedidos = listos)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar pedidos pendientes de cobro",
                    snackbarType = SnackbarType.ERROR
                )
                Log.e("PendienteCobro", "Error al cargar pedidos", it)
            }
        }
    }

    fun toggleExpandido(mesa: String) {
        val actuales = _uiState.value.pedidosExpandidos.toMutableSet()
        if (!actuales.add(mesa)) actuales.remove(mesa)
        _uiState.value = _uiState.value.copy(pedidosExpandidos = actuales)
    }

    /**
     * Devuelve una lista de pares (Producto, cantidad)
     * agrupando todas las unidades preparados y entregados.
     */
    fun getLineasAgrupadasPorMesa(pedido: Pedido): List<Pair<Producto, Int>> {
        return pedido.carta
            // para cada ProductoPedido, extraemos N veces su .producto
            .flatMap { pp ->
                pp.unidades
                    .filter { it.preparado && it.entregado }
                    .map { pp.producto }
            }
            // agrupamos idénticos y contamos
            .groupingBy { it }
            .eachCount()
            // lo convertimos en lista de pares
            .map { (producto, cantidad) -> producto to cantidad }
    }

    /** Marca un pedido como cobrado (CERRADO) y le calcula el total */
    fun cobrarPedido(pedido: Pedido) = viewModelScope.launch {
        //Calcular el total
        val total = pedido.carta.sumOf { pp -> pp.producto.price * pp.unidades.size }

        // 2. Crear copia con estado CERRADO y total calculado
        val actualizado = pedido.copy(
            state = EstadoPedido.CERRADO,
            total = total
        )

        // 3. Guardar en Firestore
        firestoreRepository.actualizarPedidoPorMesa(actualizado)
            .onSuccess {
                val mesaParaLiberar = Mesa(name = pedido.mesa, occupied = false)
                firestoreRepository.actualizarMesa(mesaParaLiberar)
                    .onSuccess {
                        // 5) Actualizar UI y notificar
                        _uiState.value = _uiState.value.copy(
                            message = "Mesa ${pedido.mesa.name} cobrada (€$total) y liberada",
                            snackbarType = SnackbarType.SUCCESS
                        )
                        _cobradoEvent.send(actualizado)
                        loadPedidosListos()
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(
                            message = "Pedido cobrado pero no se liberó mesa ${pedido.mesa.name}",
                            snackbarType = SnackbarType.ERROR
                        )
                        loadPedidosListos()
                    }
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cobrar mesa ${pedido.mesa.name}",
                    snackbarType = SnackbarType.ERROR
                )
            }
    }


    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            snackbarType = SnackbarType.INFO
        )
    }
}

data class PendienteCobroUiState(
    val pedidos: List<Pedido> = emptyList(),
    val pedidosExpandidos: Set<String> = emptySet(),
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)