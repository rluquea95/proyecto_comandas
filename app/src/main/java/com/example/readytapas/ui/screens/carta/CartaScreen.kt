package com.example.readytapas.ui.screens.carta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.CategoriaChips
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.ImagenProductoDialog
import com.example.readytapas.ui.components.ProductoCard
import com.example.readytapas.ui.components.SearchBar
import com.example.readytapas.ui.components.TopBarWithMenu

@Composable
fun CartaScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: CartaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val productos by viewModel.productosFiltrados.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensaje en snackbar cuando se detecta un error
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
                title = "Carta",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chips de categoría
            CategoriaChips(
                categorias = listOf(null) + CategoryProducto.entries,
                selectedCategoria = state.categoriaSeleccionada,
                ordenarPorPrecio = state.ordenarPorPrecio,
                onCategoriaSeleccionada = viewModel::selectCategoria,
                onOrdenarClick = viewModel::selectOrdenPrecio
            )

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 2.dp),
                searchText = state.searchText,
                onSearchTextChange = viewModel::updateSearchText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Lista de productos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onImageClick = { viewModel.selectProducto(producto) }
                    )
                }
            }

            state.productoSeleccionado?.let { producto ->
                ImagenProductoDialog(
                    producto = producto,
                    onDismiss = { viewModel.clearSelectedProducto() }
                )
            }
        }
    }
}

@Composable
fun CartaScreenContentPreview(
    productos: List<Producto>,
    selectedCategoria: CategoryProducto? = null,
    ordenarPorPrecio: Boolean = false,
    searchText: String = "",
    onProductoClick: (Producto) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBarWithMenu(
                title = "Carta",
                titleAlignment = TextAlign.Center,
                onLogoutClick = {},
                showBackButton = true,
                onBackClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CategoriaChips(
                categorias = listOf(null) + CategoryProducto.entries,
                selectedCategoria = selectedCategoria,
                ordenarPorPrecio = ordenarPorPrecio,
                onCategoriaSeleccionada = {},
                onOrdenarClick = {}
            )

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 2.dp),
                searchText = searchText,
                onSearchTextChange = {}
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onImageClick = { onProductoClick(producto) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartaScreen() {
    val mockProductos = listOf(
        Producto(
            name = "Tortilla de Patatas",
            description = "Clásica tortilla española con cebolla",
            category = CategoryProducto.PLATO,
            price = 8.50,
            imageUrl = "plato_tortilla"
        ),
        Producto(
            name = "Cerveza",
            description = "Bien fresquita",
            category = CategoryProducto.BEBIDA,
            price = 2.0,
            imageUrl = "bebida_cerveza"
        )
    )
    CartaScreenContentPreview(productos = mockProductos)
}

