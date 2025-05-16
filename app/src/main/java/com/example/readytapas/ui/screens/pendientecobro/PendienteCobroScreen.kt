package com.example.readytapas.ui.screens.pendientecobro

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.readytapas.utils.PdfTicket
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad

@Composable
fun PendienteCobroScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: PendienteCobroViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Aquí arranca el manejador de eventos de cobro y PDF
    GenerarPdfTicketHandler(viewModel)

    PendienteCobroContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onLogoutClick = onLogoutClick,
        onToggleExpandido = viewModel::toggleExpandido,
        onCobrarPedido = viewModel::cobrarPedido
    )
}

@Composable
fun PendienteCobroContent(
    state: PendienteCobroUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onToggleExpandido: (String) -> Unit,
    onCobrarPedido: (Pedido) -> Unit
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
                title = "Pendiente de Cobro",
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
            items(state.pedidos, key = { it.mesa.name }) { pedido ->
                val mesaName = pedido.mesa.name
                val isExpanded = mesaName in state.pedidosExpandidos

                val lineasAgrupadas = pedido.carta
                    .flatMap { it.unidades.map { u -> it.producto } }
                    .groupingBy { it }
                    .eachCount()
                    .map { (producto, cantidad) -> producto to cantidad }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onToggleExpandido(mesaName) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeClaro)
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = mesaName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MarronOscuro
                            )
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
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MarronMedioAcento
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "€${"%.2f".format(producto.price * cantidad)}",
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MarronOscuro
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Button(
                                    onClick = { onCobrarPedido(pedido) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MarronOscuro,
                                        contentColor = BlancoHueso
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cobrar mesa", fontSize = 16.sp)
                                }
                            }
                        }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PendienteCobroContentPreview() {
    val mockPedido = Pedido(
        mesa = NumeroMesa.MESA_3,
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
        state = EstadoPedido.LISTO
    )

    val mockState = PendienteCobroUiState(
        pedidos = listOf(mockPedido),
        pedidosExpandidos = setOf("MESA_3")
    )

    PendienteCobroContent(
        state = mockState,
        snackbarHostState = remember { SnackbarHostState() },
        onBackClick = {},
        onLogoutClick = {},
        onToggleExpandido = {},
        onCobrarPedido = {}
    )
}
