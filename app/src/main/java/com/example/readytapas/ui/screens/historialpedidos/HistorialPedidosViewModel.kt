package com.example.readytapas.ui.screens.historialpedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.repository.FirestoreRepository
import com.example.readytapas.ui.components.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistorialPedidosViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialPedidosUiState())
    val uiState: StateFlow<HistorialPedidosUiState> = _uiState

    private val _pdfEvent = Channel<Pedido>(Channel.BUFFERED)
    val pdfEvent = _pdfEvent.receiveAsFlow()

    init {
        observePedidosCerrados()
    }

    private fun observePedidosCerrados() {
        viewModelScope.launch {
            firestoreRepository.getPedidos().onSuccess { pedidos ->
                val cerrados = pedidos.filter { it.state == EstadoPedido.CERRADO }
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // Ordena los pedidos dentro de una fecha
                val grouped = cerrados
                    .groupBy { pedido -> sdf.format(pedido.time.toDate()) }
                    .mapValues { (_, pedidosDelDia) ->
                        pedidosDelDia.sortedByDescending { it.time.toDate() }
                    }

                // Ordenar las fechas en que se muestran los pedidos
                val sortedByDateDesc: Map<String, List<Pedido>> = grouped
                    .toSortedMap(compareByDescending { dateString ->
                        LocalDate.parse(dateString)  // java.time.LocalDate
                    })
                _uiState.value = _uiState.value.copy(
                    pedidosPorFecha = sortedByDateDesc
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = "Error al cargar historial de pedidos",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun toggleExpandido(pedidoId: String) {
        val actuales = _uiState.value.pedidosExpandidos.toMutableSet()
        if (!actuales.add(pedidoId)) actuales.remove(pedidoId)
        _uiState.value = _uiState.value.copy(pedidosExpandidos = actuales)
    }

    fun visualizarPdf(pedido: Pedido) {
        viewModelScope.launch {
            _pdfEvent.send(pedido)
        }
    }

    fun getLineasAgrupadas(pedido: Pedido): List<Pair<Producto, Int>> {
        return pedido.carta.flatMap { it.unidades.map { _ -> it.producto } }
            .groupingBy { it }
            .eachCount()
            .map { (producto, cantidad) -> producto to cantidad }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class HistorialPedidosUiState(
    val pedidosPorFecha: Map<String, List<Pedido>> = emptyMap(),
    val pedidosExpandidos: Set<String> = emptySet(),
    val message: String? = null,
    val snackbarType: SnackbarType = SnackbarType.INFO
)
