package com.example.readytapas.ui.screens.platoslistos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import com.example.readytapas.data.model.*
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad


@Composable
fun PlatosListosScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: PlatosListosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    PlatosListosContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onLogoutClick = onLogoutClick,
        onToggleExpandido = viewModel::toggleExpandido,
        onToggleSeleccionUnidad = viewModel::toggleSeleccionUnidad,
        onConfirmEntregados = viewModel::confirmEntregados,
        getPlatosPendientesPorMesa = viewModel::getPlatosPendientesPorMesa
    )
}

@Composable
fun PlatosListosContent(
    state: PlatosListosUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onToggleExpandido: (String) -> Unit,
    onToggleSeleccionUnidad: (String, String) -> Unit,
    onConfirmEntregados: () -> Unit,
    getPlatosPendientesPorMesa: (String) -> List<Pair<ProductoPedido, Int>>
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
                title = "Platos Listos",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirmEntregados,
                enabled = state.productosSeleccionados.any { it.value.isNotEmpty() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarronOscuro,
                    contentColor = BlancoHueso
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar entregados", fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val pedidosFiltrados = state.pedidos.filter {
                    getPlatosPendientesPorMesa(it.mesa.name).isNotEmpty()
                }

                items(pedidosFiltrados, key = { it.mesa.name }) { pedido ->
                    val mesaName = pedido.mesa.name
                    val isExpanded = mesaName in state.pedidosExpandidos
                    val platosPendientes = getPlatosPendientesPorMesa(mesaName)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onToggleExpandido(mesaName) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MarronMedioAcentoOpacidad),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = mesaName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MarronOscuro
                            )
                            if (isExpanded) {
                                Spacer(Modifier.height(8.dp))

                                if (platosPendientes.isEmpty()) {
                                    Text(
                                        "Todos los platos entregados.",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MarronOscuro
                                    )
                                } else {
                                    platosPendientes.forEach { (pp, idx) ->
                                        val clave = "${pp.producto.name}-$idx"
                                        val seleccionado = state.productosSeleccionados[mesaName]?.contains(clave) == true

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(BeigeClaro, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Checkbox(
                                                checked = seleccionado,
                                                onCheckedChange = {
                                                    onToggleSeleccionUnidad(mesaName, clave)
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = MarronOscuro,
                                                    uncheckedColor = Color.Gray,
                                                    checkmarkColor = BlancoHueso
                                                )
                                            )
                                            Text(
                                                text = pp.producto.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MarronOscuro
                                            )
                                        }
                                    }
                                }
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
fun PreviewPlatosListosContent() {
    val mockProducto = Producto(name = "Tortilla", category = CategoryProducto.PLATO)
    val mockPedido = Pedido(
        mesa = NumeroMesa.MESA_2,
        carta = listOf(
            ProductoPedido(
                producto = mockProducto,
                unidades = listOf(
                    EstadoUnidad(preparado = true, entregado = false),
                    EstadoUnidad(preparado = true, entregado = false)
                )
            )
        )
    )

    val mockState = PlatosListosUiState(
        pedidos = listOf(mockPedido),
        pedidosExpandidos = setOf("MESA_2"),
        productosSeleccionados = mapOf("MESA_2" to setOf("Tortilla-0"))
    )

    val snackbarHostState = remember { SnackbarHostState() }

    PlatosListosContent(
        state = mockState,
        snackbarHostState = snackbarHostState,
        onBackClick = {},
        onLogoutClick = {},
        onToggleExpandido = {},
        onToggleSeleccionUnidad = { _, _ -> },
        onConfirmEntregados = {},
        getPlatosPendientesPorMesa = {
            listOf(ProductoPedido(producto = mockProducto, unidades = listOf()) to 0)
        }
    )
}


