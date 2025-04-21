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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarBlancoHuesoTexto
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

    val categorias = listOf<CategoryProducto?>(null) + CategoryProducto.entries

    Column {
        TopBarWithMenu(
            title = "Carta",
            titleAlignment = TextAlign.Center,
            onLogoutClick = onLogoutClick,
            showBackButton = true,
            onBackClick = { navController.popBackStack() }
        )
        // Chips de categoría
        CategoriaChips(
            categorias = listOf(null) + CategoryProducto.entries,
            selectedCategoria = selectedCategoria,
            ordenarPorPrecio = ordenarPorPrecio,
            onCategoriaSeleccionada = viewModel::seleccionarCategoria,
            onOrdenarClick = viewModel::alternarOrdenPrecio
        )
        // Lista de productos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
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
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BarMarronMedioAcento),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Imagen (si tienes Coil o Glide Compose)
            AsyncImage(
                model = producto.imageUrl,
                contentDescription = producto.name,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    producto.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    producto.description,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.widthIn(max = 180.dp) // Limita el ancho para que salte de línea
                )
            }
            // Precio centrado verticalmente
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .align(Alignment.CenterVertically), // 🔹 Esto centra verticalmente la columna completa en el Row
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${producto.price} €",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

//Composable encargado de dibujar los chips de categoria y precio
@Composable
fun CategoriaChips(
    categorias: List<CategoryProducto?>,
    selectedCategoria: CategoryProducto?,
    ordenarPorPrecio: Boolean? = null,
    onCategoriaSeleccionada: (CategoryProducto?) -> Unit,
    onOrdenarClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        categorias.forEach { categoria ->
            val esSeleccionada = categoria == selectedCategoria
            val nombre = categoria?.name ?: "TODOS"

            FilterChip(
                selected = esSeleccionada,
                onClick = { onCategoriaSeleccionada(categoria) },
                label = {
                    Text(
                        nombre.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        },
                        color = if (esSeleccionada) BarBlancoHuesoTexto else BarMarronMedioAcento
                    )
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BarBeigeClaro,
                    selectedContainerColor = BarMarronMedioAcento,
                    labelColor = BarMarronOscuro,
                    selectedLabelColor = BarBlancoHuesoTexto
                )
            )
        }

        if (ordenarPorPrecio != null && onOrdenarClick != null) {
            FilterChip(
                selected = ordenarPorPrecio,
                onClick = onOrdenarClick,
                label = {
                    Text(
                        "PRECIO",
                        color = if (ordenarPorPrecio) BarBlancoHuesoTexto else BarMarronMedioAcento
                    )
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BarBeigeClaro,
                    selectedContainerColor = BarMarronMedioAcento,
                    labelColor = BarMarronOscuro,
                    selectedLabelColor = BarBlancoHuesoTexto
                )
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

    Column {
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosMock) { producto ->
                ProductoCard(producto)
            }
        }
    }
}

