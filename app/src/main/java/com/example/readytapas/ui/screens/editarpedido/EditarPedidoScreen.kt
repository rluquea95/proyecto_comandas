package com.example.readytapas.ui.screens.editarpedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.EstadoUnidad
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.ui.components.AgregarProductoDialog
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.MesaDropdown
import com.example.readytapas.ui.components.ProductoPedidoItem
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.screens.tomarpedido.TomarPedidoContent
import com.example.readytapas.ui.screens.tomarpedido.TomarPedidoUiState
import com.example.readytapas.ui.screens.tomarpedido.TomarPedidoViewModel
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronOscuro
import kotlinx.coroutines.launch

@Composable
fun EditarPedidoScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: EditarPedidoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val productosFiltrados by viewModel.productosFiltrados.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    EditarPedidoContent(
        state = state,
        productosFiltrados = productosFiltrados,
        snackbarHostState = snackbarHostState,
        onMesaSeleccionada = viewModel::selectMesa,
        onSearchTextChange = viewModel::updateSearchText,
        onCategoriaSeleccionada = viewModel::selectCategoria,
        onProductoSeleccionado = viewModel::addProducto,
        onAumentar = viewModel::increaseCantidad,
        onDisminuir = viewModel::decreaseCantidad,
        onEliminar = viewModel::removeProducto,
        onConfirmCambios = viewModel::confirmEdicion,
        onEliminarPedido = viewModel::eliminarPedido,
        onLogoutClick = onLogoutClick,
        onBackClick = {
                viewModel.clearLockAndCancel()
                navController.popBackStack()
        }
    )
}

@Composable
fun EditarPedidoContent(
    state: EditarPedidoUiState,
    productosFiltrados: List<Producto>,
    snackbarHostState: SnackbarHostState,
    onMesaSeleccionada: (Mesa) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onCategoriaSeleccionada: (CategoryProducto?) -> Unit,
    onProductoSeleccionado: (Producto) -> Unit,
    onAumentar: (ProductoPedido) -> Unit,
    onDisminuir: (ProductoPedido) -> Unit,
    onEliminar: (ProductoPedido) -> Unit,
    onConfirmCambios: () -> Unit,
    onEliminarPedido: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

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
                title = "Editar Pedido",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onConfirmCambios,
                    enabled = state.mesaSeleccionada != null && state.productosPedidos.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarronOscuro,
                        contentColor = BlancoHueso
                    )
                ) {
                    Text("Guardar Cambios")
                }

                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = state.mesaSeleccionada != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = BlancoHueso
                    )
                ) {
                    Text("Eliminar Pedido")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Si está bloqueado, avisamos al usuario
            if (state.pedidoBloqueado) {
                Text(
                    text = "Este pedido está siendo editado por otro usuario",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33FF0000))
                        .padding(8.dp)
                )
            }

            MesaDropdown(
                mesas = state.mesas,
                mesaSeleccionada = state.mesaSeleccionada,
                onMesaSeleccionada = onMesaSeleccionada,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { isDialogOpen = true },
                enabled = state.mesaSeleccionada != null && !state.pedidoBloqueado,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarronOscuro,
                    contentColor = BlancoHueso
                )
            ) {
                Text("Añadir Producto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.productosPedidos.isNotEmpty()) {
                Text(
                    text = "Pedido actual:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.productosPedidos) { productoPedido ->
                        ProductoPedidoItem(
                            productoPedido = productoPedido,
                            onAumentar = { onAumentar(productoPedido) },
                            onDisminuir = { onDisminuir(productoPedido) },
                            onEliminar = { onEliminar(productoPedido) }
                        )
                    }
                }
            }
        }

        if (isDialogOpen) {
            AgregarProductoDialog(
                productos = productosFiltrados,
                categoriaSeleccionada = state.categoriaSeleccionada,
                searchText = state.searchText,
                onSearchTextChange = onSearchTextChange,
                onCategoriaSeleccionada = onCategoriaSeleccionada,
                onProductoSeleccionado = {
                    onProductoSeleccionado(it)
                    isDialogOpen = false
                },
                onDismiss = { isDialogOpen = false }
            )
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("¿Eliminar pedido?") },
                text = { Text("¿Estás seguro de que quieres eliminar este pedido?") },
                confirmButton = {
                    TextButton(onClick = {
                        onEliminarPedido()
                        showConfirmDialog = false
                    }) {
                        Text("Sí", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarPedidoContentPreview() {
    val mesas = listOf(Mesa(name = NumeroMesa.MESA_1))
    val productos = listOf(
        Producto(name = "Cerveza", price=2.0, category = CategoryProducto.BEBIDA),
        Producto(name = "Tortilla", price=6.5, category = CategoryProducto.PLATO)
    )
    val productosPedidos = listOf(
        ProductoPedido(productos[0], listOf(EstadoUnidad(), EstadoUnidad())),
        ProductoPedido(productos[1], listOf(EstadoUnidad()))
    )

    val mockState = EditarPedidoUiState(
        mesas = mesas,
        mesaSeleccionada = mesas.first(),
        productos = productos,
        productosPedidos = productosPedidos
    )

    EditarPedidoContent(
        state = mockState,
        productosFiltrados = productos,
        snackbarHostState = remember { SnackbarHostState() },
        onMesaSeleccionada = {},
        onSearchTextChange = {},
        onCategoriaSeleccionada = {},
        onProductoSeleccionado = {},
        onAumentar = {},
        onDisminuir = {},
        onEliminar = {},
        onConfirmCambios = {},
        onEliminarPedido = {},
        onLogoutClick = {},
        onBackClick = {}
    )
}




