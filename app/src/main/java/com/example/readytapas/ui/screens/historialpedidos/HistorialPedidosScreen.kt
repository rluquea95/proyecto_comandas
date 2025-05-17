package com.example.readytapas.ui.screens.historialpedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.EstadoUnidad
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.screens.pendientecobro.GenerarPdfTicketHandler
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronOscuro
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistorialPedidosScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: HistorialPedidosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    GenerarPdfTicketHandler(
        pedidosFlow = viewModel.pdfEvent,
        getLineas = viewModel::getLineasAgrupadas
    )

    HistorialPedidosContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onLogoutClick = onLogoutClick,
        onToggleExpandido = viewModel::toggleExpandido,
        onVerPdfClick = viewModel::visualizarPdf,
        getLineasAgrupadas = viewModel::getLineasAgrupadas
    )
}

@Composable
fun HistorialPedidosContent(
    state: HistorialPedidosUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onToggleExpandido: (String) -> Unit,
    onVerPdfClick: (Pedido) -> Unit,
    getLineasAgrupadas: (Pedido) -> List<Pair<Producto, Int>>
) {
    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                snackbarType = state.snackbarType,
                onDismiss = {}
            )
        },
        topBar = {
            TopBarWithMenu(
                title = "Historial de Pedidos",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            state.pedidosPorFecha.forEach { (fecha, pedidos) ->
                item {
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(pedidos, key = { "${it.mesa.name}-${it.time.seconds}" }) { pedido ->
                    val pedidoId = "${pedido.mesa.name}-${pedido.time.seconds}"
                    val isExpanded = state.pedidosExpandidos.contains(pedidoId)
                    val lineasAgrupadas = getLineasAgrupadas(pedido)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onToggleExpandido(pedidoId) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BeigeClaro)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pedido.mesa.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MarronOscuro
                                )

                                val fechaFormateada = remember(pedido.time) {
                                    val sdf =
                                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    sdf.format(pedido.time.toDate())
                                }

                                Text(
                                    text = fechaFormateada,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.Gray
                                )
                            }
                            if (isExpanded) {
                                Spacer(Modifier.height(8.dp))
                                // Fondo tipo ticket
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = BlancoHueso,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    lineasAgrupadas.forEach { (producto, cantidad) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$cantidad×  ${producto.name}",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MarronMedioAcento
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "€${"%.2f".format(producto.price * cantidad)}",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MarronOscuro
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onVerPdfClick(pedido) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MarronOscuro,
                                        contentColor = BlancoHueso
                                    )
                                ) {
                                    Text("Visualizar en PDF")
                                }
                            }
                        }
                    }
                }
                item {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(thickness = 2.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialPedidosContentPreview() {
    val mockPedido = Pedido(
        mesa = NumeroMesa.MESA_5,
        carta = listOf(
            ProductoPedido(
                producto = Producto(name = "Coca-Cola", price = 1.5, category = CategoryProducto.BEBIDA),
                unidades = List(2) { EstadoUnidad(preparado = true, entregado = true) }
            ),
            ProductoPedido(
                producto = Producto(name = "Montadito de jamón", price = 2.0, category = CategoryProducto.TAPA),
                unidades = List(1) { EstadoUnidad(preparado = true, entregado = true) }
            )
        ),
        state = EstadoPedido.CERRADO,
        total = 6.0
    )

    val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(mockPedido.time.toDate())

    val mockPedidosPorFecha = mapOf(
        fecha to listOf(mockPedido)
    )

    val mockState = HistorialPedidosUiState(
        pedidosPorFecha = mockPedidosPorFecha,
        pedidosExpandidos = setOf("${mockPedido.mesa.name}-${mockPedido.time.seconds}"),
    )

    val mockGetLineasAgrupadas: (Pedido) -> List<Pair<Producto, Int>> = { pedido ->
        pedido.carta
            .flatMap { pp -> pp.unidades.map { pp.producto } }
            .groupingBy { it }
            .eachCount()
            .map { (producto, cantidad) -> producto to cantidad }
    }

    HistorialPedidosContent(
        state = mockState,
        snackbarHostState = remember { SnackbarHostState() },
        onBackClick = {},
        onLogoutClick = {},
        onToggleExpandido = {},
        onVerPdfClick = {},
        getLineasAgrupadas = mockGetLineasAgrupadas
    )
}






