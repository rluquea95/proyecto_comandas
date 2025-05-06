package com.example.readytapas.ui.screens.enpreparacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    // Mostrar mensaje snackbar
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState, isError = state.isError)
        },
        topBar = {
            TopBarWithMenu(
                title = "En Preparación",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            Button(
                onClick = viewModel::confirmPreparados,
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
                Text("Confirmar preparados")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Switch Camarero/Cocina
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
                            viewModel.changeVista(
                                if (index == 0) VistaPreparacion.CAMARERO else VistaPreparacion.COCINA
                            )
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
                val pedidosFiltrados = state.pedidos.filter { pedido ->
                    val mesaName = pedido.mesa.name
                    viewModel.getProductosPendientesPorMesa(mesaName).isNotEmpty()
                }

                items(items = pedidosFiltrados, key = { it.mesa.name }) { pedido ->
                    val mesaName = pedido.mesa.name
                    val isExpanded = state.pedidosExpandidos.contains(mesaName)
                    val productosPendientes = viewModel.getProductosPendientesPorMesa(mesaName)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.toggleExpandido(mesaName) },
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
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
                                                    viewModel.toggleSeleccionUnidad(mesaName, clave)
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
fun EnPreparacionScreenContentPreview() {
    val mockProducto1 = Producto(name = "Caña", category = CategoryProducto.BEBIDA)
    val mockProducto2 = Producto(name = "Tortilla", category = CategoryProducto.PLATO)

    val mockPedido = Pedido(
        mesa = NumeroMesa.MESA_1,
        carta = listOf(
            ProductoPedido(
                producto = mockProducto1,
                unidades = listOf(
                    EstadoUnidad(preparado = false),
                    EstadoUnidad(preparado = true)
                )
            ),
            ProductoPedido(
                producto = mockProducto2,
                unidades = listOf(
                    EstadoUnidad(preparado = false),
                    EstadoUnidad(preparado = false)
                )
            )
        )
    )

    val previewState = EnPreparacionUiState(
        pedidos = listOf(mockPedido),
        pedidosExpandidos = setOf("MESA_1"),
        productosSeleccionados = mapOf("MESA_1" to setOf("Caña-0", "Tortilla-1")),
        vista = VistaPreparacion.COCINA
    )

    EnPreparacionScreenContentPreview(state = previewState)
}

@Composable
fun EnPreparacionScreenContentPreview(state: EnPreparacionUiState) {
    Scaffold(
        topBar = {
            TopBarWithMenu(
                title = "En preparación",
                titleAlignment = TextAlign.Center,
                onLogoutClick = {},
                showBackButton = false,
                onBackClick = {}
            )
        },
        bottomBar = {
            Button(
                onClick = {},
                enabled = state.productosSeleccionados.any { it.value.isNotEmpty() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Confirmar preparados")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Camarero")
                Switch(checked = state.vista == VistaPreparacion.COCINA, onCheckedChange = {})
                Text("Cocina")
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.pedidos) { pedido ->
                    val mesa = pedido.mesa.name
                    val isExpanded = state.pedidosExpandidos.contains(mesa)

                    // ✅ Mostrar una línea por unidad no preparada (en ambas vistas)
                    val productosPendientes = pedido.carta.flatMap { producto ->
                        producto.unidades.mapIndexedNotNull { idx, unidad ->
                            if (!unidad.preparado &&
                                when (state.vista) {
                                    VistaPreparacion.CAMARERO -> producto.producto.category == CategoryProducto.BEBIDA
                                    VistaPreparacion.COCINA -> producto.producto.category != CategoryProducto.BEBIDA
                                }
                            ) {
                                producto to idx
                            } else null
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mesa: $mesa", style = MaterialTheme.typography.titleMedium)

                            if (isExpanded) {
                                Spacer(Modifier.height(8.dp))

                                if (productosPendientes.isEmpty()) {
                                    Text("Todos los productos están preparados.")
                                } else {
                                    productosPendientes.forEach { (productoPedido, idx) ->
                                        val clave = "${productoPedido.producto.name}-$idx"
                                        val seleccionado = state.productosSeleccionados[mesa]?.contains(clave) == true

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Checkbox(
                                                checked = seleccionado,
                                                onCheckedChange = {},
                                                enabled = false
                                            )
                                            Text("${productoPedido.producto.name} (${idx + 1})")
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