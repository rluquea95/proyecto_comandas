package com.example.readytapas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronOscuro

@Composable
fun AgregarProductoDialog(
    productos: List<Producto>,
    categoriaSeleccionada: CategoryProducto?,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCategoriaSeleccionada: (CategoryProducto?) -> Unit,
    onProductoSeleccionado: (Producto) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoHueso),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seleccionar Producto",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MarronOscuro
                )

                Spacer(modifier = Modifier.height(8.dp))

                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    searchText = searchText,
                    onSearchTextChange = onSearchTextChange
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoriaChips(
                    categorias = listOf(null) + CategoryProducto.entries,
                    selectedCategoria = categoriaSeleccionada,
                    ordenarPorPrecio = null,
                    onCategoriaSeleccionada = onCategoriaSeleccionada
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productos) { producto ->
                        ProductoPedidoDialogItem(
                            producto = producto,
                            onAgregar = { onProductoSeleccionado(producto) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarronOscuro,
                        contentColor = BlancoHueso
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgregarProductoDialogPreview() {
    val productosMock = listOf(
        Producto(
            name = "Tortilla",
            description = "Tortilla española",
            category = CategoryProducto.PLATO,
            price = 8.0,
            imageUrl = "plato_tortilla"
        ),
        Producto(
            name = "Cerveza",
            description = "Caña fría",
            category = CategoryProducto.BEBIDA,
            price = 2.0,
            imageUrl = "bebida_cerveza"
        )
    )

    val searchText = remember { mutableStateOf("") }

    AgregarProductoDialog(
        productos = productosMock,
        categoriaSeleccionada = null,
        searchText = searchText.value,
        onSearchTextChange = { searchText.value = it },
        onCategoriaSeleccionada = {},
        onProductoSeleccionado = {},
        onDismiss = {}
    )
}