package com.example.readytapas.ui.screens.carta

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.TopBarWithMenu
import java.util.Locale

@Composable
fun CartaScreen(
    onLogoutClick: () -> Unit,
    navController: NavController,
    viewModel: CartaViewModel = hiltViewModel()
) {
    val productos by viewModel.productosFiltrados.collectAsState()
    val selectedCategoria by viewModel.selectedCategoria.collectAsState()

    val categorias = listOf<CategoryProducto?>(null) + CategoryProducto.entries

    Column {
        TopBarWithMenu(onLogoutClick = onLogoutClick) // Pasamos la acción de logout
        // Chips de categoría
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            categorias.forEach { categoria ->
                val esSeleccionada = categoria == selectedCategoria
                val nombre = categoria?.name ?: "Todos"

                FilterChip(
                    selected = esSeleccionada,
                    onClick = { viewModel.seleccionarCategoria(categoria) },
                    label = { Text(nombre.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // Lista de productos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                ProductoCard(producto)
            }
        }
    }
}

@Composable
fun ProductoCard(producto: Producto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Imagen (si tienes Coil o Glide Compose)
            AsyncImage(
                model = producto.imageUrl,
                contentDescription = producto.name,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(producto.name, style = MaterialTheme.typography.titleMedium)
                Text(producto.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                Text("€${producto.price}", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

//Preparar la Preview
@Composable
fun CartaScreenPreviewContent(
    productos: List<Producto>,
    selectedCategoria: CategoryProducto? = null,
    onCategoriaSeleccionada: (CategoryProducto?) -> Unit = {}
) {
    val categorias = listOf<CategoryProducto?>(null) + CategoryProducto.entries

    Column {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            categorias.forEach { categoria ->
                val esSeleccionada = categoria == selectedCategoria
                val nombre = categoria?.name ?: "Todos"

                FilterChip(
                    selected = esSeleccionada,
                    onClick = { onCategoriaSeleccionada(categoria) },
                    label = { Text(nombre.replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                ProductoCard(producto)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartaScreenPreview() {
    val productosMock = listOf(
        Producto(
            name = "Tortilla de patatas",
            description = "Clásica tortilla española con cebolla",
            category = CategoryProducto.PLATO,
            price = 8.50,
            imageUrl = ""
        ),
        Producto(
            name = "Cerveza",
            description = "Caña bien fría",
            category = CategoryProducto.BEBIDA,
            price = 2.0,
            imageUrl = ""
        )
    )

    CartaScreenPreviewContent(
        productos = productosMock,
        selectedCategoria = null
    )
}

