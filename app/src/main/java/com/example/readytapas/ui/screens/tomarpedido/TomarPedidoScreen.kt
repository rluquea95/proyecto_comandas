package com.example.readytapas.ui.screens.tomarpedido

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronOscuro
import com.example.readytapas.ui.components.*
import com.example.readytapas.ui.components.CustomSnackbarHost


@Composable
fun TomarPedidoScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: TomarPedidoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val productosFiltrados by viewModel.productosFiltrados.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                isError = state.isError
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.confirmPedido() },
                enabled = state.mesaSeleccionada != null && state.productosPedidos.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarronOscuro,
                    contentColor = BlancoHueso
                )
            ) {
                Text("Confirmar Pedido")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopBarWithMenu(
                title = "Tomar Pedido",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MesaDropdown(
                mesas = state.mesas,
                mesaSeleccionada = state.mesaSeleccionada,
                onMesaSeleccionada = viewModel::selectMesa
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { isDialogOpen = true },
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
                            onAumentar = { viewModel.increaseCantidad(productoPedido) },
                            onDisminuir = { viewModel.decreaseCantidad(productoPedido) },
                            onEliminar = { viewModel.removeProducto(productoPedido) }
                        )
                    }
                }
            }
        }
    }

    if (isDialogOpen) {
        AgregarProductoDialog(
            productos = productosFiltrados,
            categoriaSeleccionada = state.categoriaSeleccionada,
            searchText = state.searchText,
            onSearchTextChange = viewModel::updateSearchText,
            onCategoriaSeleccionada = viewModel::selectCategoria,
            onProductoSeleccionado = { producto ->
                viewModel.addProducto(producto)
            },
            onDismiss = { isDialogOpen = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TomarPedidoScreenPreview() {
    val mesasMock = listOf(
        Mesa(name = NumeroMesa.MESA_1, occupied = false),
        Mesa(name = NumeroMesa.MESA_2, occupied = false),
        Mesa(name = NumeroMesa.BARRA, occupied = false)
    )

    val productosMock = listOf(
        Producto(name = "Tortilla", description = "", category = CategoryProducto.PLATO, price = 8.0, imageUrl = "plato_tortilla"),
        Producto(name = "Caña", description = "", category = CategoryProducto.BEBIDA, price = 2.0, imageUrl = "bebida_cerveza")
    )

    val productosPedidosMock = listOf(
        ProductoPedido(producto = productosMock[0], cantidad = 2),
        ProductoPedido(producto = productosMock[1], cantidad = 1)
    )

    // Modo fake, sin ViewModel
    TomarPedidoScreenContentPreview(
        mesas = mesasMock,
        mesaSeleccionada = mesasMock[0],
        productosPedidos = productosPedidosMock
    )
}

@Composable
fun TomarPedidoScreenContentPreview(
    mesas: List<Mesa>,
    mesaSeleccionada: Mesa?,
    productosPedidos: List<ProductoPedido>
) {
    Scaffold(
        bottomBar = {
            Button(
                onClick = {},
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Confirmar Pedido")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopBarWithMenu(
                title = "Tomar Pedido",
                titleAlignment = TextAlign.Center,
                onLogoutClick = {},
                showBackButton = true,
                onBackClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            MesaDropdown(
                mesas = mesas,
                mesaSeleccionada = mesaSeleccionada,
                onMesaSeleccionada = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                items(productosPedidos) { productoPedido ->
                    ProductoPedidoItem(
                        productoPedido = productoPedido,
                        onAumentar = {},
                        onDisminuir = {},
                        onEliminar = {}
                    )
                }
            }
        }
    }
}



