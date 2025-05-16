package com.example.readytapas.ui.screens.enpreparacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoUnidad
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad
import com.example.readytapas.ui.theme.MarronOscuro

@Composable
fun EnPreparacionScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: EnPreparacionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    EnPreparacionContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onLogoutClick = onLogoutClick,
        onBackClick = { navController.popBackStack() },
        onConfirmPreparados = viewModel::confirmPreparados,
        onChangeVista = viewModel::changeVista,
        onToggleExpandido = viewModel::toggleExpandido,
        onToggleSeleccionUnidad = viewModel::toggleSeleccionUnidad,
        getPedidosConPendientes = viewModel::getPedidosConPendientes,
        getProductosPendientesPorMesa = viewModel::getProductosPendientesPorMesa
    )
}

@Composable
fun EnPreparacionContent(
    state: EnPreparacionUiState,
    snackbarHostState: SnackbarHostState,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmPreparados: () -> Unit,
    onChangeVista: (VistaPreparacion) -> Unit,
    onToggleExpandido: (String) -> Unit,
    onToggleSeleccionUnidad: (String, String) -> Unit,
    getPedidosConPendientes: () -> List<Pedido>,
    getProductosPendientesPorMesa: (String) -> List<Pair<ProductoPedido, Int>>
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
                title = "En Preparación",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirmPreparados,
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

            val selectedTabIndex = when (state.vista) {
                VistaPreparacion.CAMARERO -> 0
                VistaPreparacion.COCINA -> 1
            }

            val tabs = listOf("CAMARERO", "COCINA")

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MarronOscuro
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            onChangeVista(if (index == 0) VistaPreparacion.CAMARERO else VistaPreparacion.COCINA)
                        },
                        modifier = Modifier.background(
                            if (selectedTabIndex == index) MarronMedioAcento else BeigeClaro
                        ),
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (selectedTabIndex == index) BlancoHueso else Color.Gray
                            )
                        }
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val pedidosFiltrados = getPedidosConPendientes()

                items(items = pedidosFiltrados, key = { it.mesa.name }) { pedido ->
                    val mesaName = pedido.mesa.name
                    val isExpanded = state.pedidosExpandidos[state.vista]?.contains(mesaName) == true
                    val productosPendientes = getProductosPendientesPorMesa(mesaName)

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

                                if (productosPendientes.isEmpty()) {
                                    Text(
                                        "Todos los productos están preparados.",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MarronOscuro
                                    )
                                } else {
                                    productosPendientes.forEach { (productoPedido, idx) ->
                                        val clave = "${productoPedido.producto.name}-$idx"
                                        val seleccionado = state.productosSeleccionados[mesaName]?.contains(clave) == true

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(
                                                    color = BeigeClaro,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
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
                                                productoPedido.producto.name,
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
fun EnPreparacionPreview() {
    val mockProducto = Producto(name = "Caña", category = CategoryProducto.BEBIDA, price = 1.5)
    val mockPedido = Pedido(
        mesa = NumeroMesa.MESA_1,
        carta = listOf(
            ProductoPedido(
                producto = mockProducto,
                unidades = listOf(
                    EstadoUnidad(preparado = false),
                    EstadoUnidad(preparado = false)
                )
            )
        )
    )

    val previewState = EnPreparacionUiState(
        pedidos = listOf(mockPedido),
        pedidosExpandidos = mapOf(
            VistaPreparacion.CAMARERO to setOf("MESA_1")
        ),
        productosSeleccionados = mapOf("MESA_1" to setOf("Caña-0")),
        vista = VistaPreparacion.CAMARERO
    )

    val snackbarHostState = remember { SnackbarHostState() }

    EnPreparacionContent(
        state = previewState,
        snackbarHostState = snackbarHostState,
        onLogoutClick = {},
        onBackClick = {},
        onConfirmPreparados = {},
        onChangeVista = {},
        onToggleExpandido = {},
        onToggleSeleccionUnidad = { _, _ -> },
        getPedidosConPendientes = { previewState.pedidos },
        getProductosPendientesPorMesa = { mesa ->
            previewState.pedidos
                .find { it.mesa.name == mesa }
                ?.carta
                ?.flatMapIndexed { i, pp ->
                    pp.unidades.mapIndexedNotNull { idx, unidad ->
                        if (!unidad.preparado) pp to idx else null
                    }
                } ?: emptyList()
        }
    )
}


