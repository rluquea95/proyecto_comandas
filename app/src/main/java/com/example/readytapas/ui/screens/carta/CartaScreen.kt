package com.example.readytapas.ui.screens.carta

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.R
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.CategoriaChips
import com.example.readytapas.ui.components.ImagenProductoDialog
import com.example.readytapas.ui.components.ProductoCard
import com.example.readytapas.ui.components.SearchBar
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarBlancoHueso
import com.example.readytapas.ui.theme.BarGrisMedio
import com.example.readytapas.ui.theme.BarMarronMedioAcento
import com.example.readytapas.ui.theme.BarMarronOscuro
import java.util.Locale

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
            searchText = searchText,
            onSearchTextChange = viewModel::actualizarTextoBusqueda,
            placeholder = "Buscar producto..."
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
            price = 8.50,
            imageUrl = "plato_tortilla"
        ),
        Producto(
            name = "Cerveza",
            description = "Caña bien fría",
            category = CategoryProducto.BEBIDA,
            price = 12.0,
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
            searchText = "",
            onSearchTextChange = {},
            placeholder = "Buscar producto..."
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