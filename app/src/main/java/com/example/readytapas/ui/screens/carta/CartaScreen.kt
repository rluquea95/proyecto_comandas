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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.CategoriaChips
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
    val productos by viewModel.productosFiltrados.collectAsState()
    val selectedCategoria by viewModel.selectedCategoria.collectAsState()
    val ordenarPorPrecio by viewModel.ordenarPorPrecio.collectAsState()
    val imagenProductoSeleccionada by viewModel.imagenProductoSeleccionada.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val categorias = listOf<CategoryProducto?>(null) + CategoryProducto.entries

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        TopBarWithMenu(
            title = "Carta",
            titleAlignment = TextAlign.Center,
            onLogoutClick = onLogoutClick,
            showBackButton = true,
            onBackClick = { navController.popBackStack() }
        )
        // Chips de categoría
        CategoriaChips(
            categorias = categorias,
            selectedCategoria = selectedCategoria,
            ordenarPorPrecio = ordenarPorPrecio,
            onCategoriaSeleccionada = viewModel::seleccionarCategoria,
            onOrdenarClick = viewModel::alternarOrdenPrecio
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp),
            searchText = searchText,
            onSearchTextChange = viewModel::actualizarTextoBusqueda
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
                    onImageClick = { viewModel.seleccionarImagenProducto(producto) }
                )
            }
        }

        imagenProductoSeleccionada?.let { producto ->
            ImagenProductoDialog(
                producto = producto,
                onDismiss = { viewModel.cerrarImagenProducto() }
            )
        }
    }
}


//Preparar la Preview
@Preview(showBackground = true)
@Composable
fun CartaScreenPreview() {
    val productosMock = listOf(
        Producto(
            name = "Tortilla de patatas",
            description = "Clásica tortilla española con cebolla",
            category = CategoryProducto.PLATO,
            price = 8.0,
            imageUrl = "plato_tortilla"
        ),
        Producto(
            name = "Cerveza",
            description = "Caña bien fría",
            category = CategoryProducto.BEBIDA,
            price = 1.5,
            imageUrl = "bebida_cerveza"
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        TopBarWithMenu(
            title = "Carta",
            titleAlignment = TextAlign.Center,
            onLogoutClick = {},
            showBackButton = true,
            onBackClick = {}
        )
        CategoriaChips(
            categorias = listOf(null) + CategoryProducto.entries,
            selectedCategoria = null,
            onCategoriaSeleccionada = {},
            ordenarPorPrecio = false,
            onOrdenarClick = {},
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 2.dp),
            searchText = "",
            onSearchTextChange = {}
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosMock) { producto ->
                ProductoCard(
                    producto = producto,
                    onImageClick = {} // Necesario ahora con nueva firma
                )
            }
        }
    }
}