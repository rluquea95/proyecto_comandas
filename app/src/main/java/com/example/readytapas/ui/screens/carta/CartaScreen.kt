package com.example.readytapas.ui.screens.carta

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readytapas.R
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
    val imagenProductoSeleccionada by viewModel.imagenProductoSeleccionada.collectAsState()
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
            categorias = categorias,
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
                ProductoCard(
                    producto = producto,
                    onImageClick = { viewModel.seleccionarImagenProducto(producto) }
                )
            }
        }

        if (imagenProductoSeleccionada != null) {
            Dialog(onDismissRequest = { viewModel.cerrarImagenProducto() }) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BarBeigeClaro)
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = loadLocalDrawableStrict(imagenProductoSeleccionada!!.imageUrl),
                            contentDescription = imagenProductoSeleccionada!!.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            imagenProductoSeleccionada!!.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            imagenProductoSeleccionada!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cerrarImagenProducto() }) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun loadLocalDrawableStrict(name: String): Painter {
    val context = LocalContext.current
    val resId = remember(name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
    return painterResource(id = resId)
}

@Composable
fun ProductoCard(
    producto: Producto,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BarMarronMedioAcento),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.wrapContentWidth()) {
                Image(
                    painter = loadLocalDrawableStrict(producto.imageUrl),
                    contentDescription = producto.name,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onImageClick() }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.wrapContentWidth()) {
                Text(
                    producto.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.width(180.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    producto.description,
                    maxLines = 4,
                    //overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(160.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Precio centrado verticalmente
            Column(modifier = Modifier.align(alignment = Alignment.CenterVertically)
            ) {
                Text(
                    "${producto.category.name}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Right,
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    "${producto.price} €",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
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

        ordenarPorPrecio?.let {
            FilterChip(
                selected = it,
                onClick = { onOrdenarClick?.invoke() },
                label = {
                    Text(
                        "PRECIO",
                        color = if (it) BarBlancoHuesoTexto else BarMarronMedioAcento
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
                ProductoCard(
                    producto = producto,
                    onImageClick = {} // Necesario ahora con nueva firma
                )
            }
        }
    }
}

