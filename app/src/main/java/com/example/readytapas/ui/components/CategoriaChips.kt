package com.example.readytapas.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronOscuro
import java.util.Locale

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
            // Verificar si es la categoría seleccionada
            val esSeleccionada = categoria == selectedCategoria
            val nombre = categoria?.name ?: "TODOS"

            FilterChip(
                selected = esSeleccionada,
                onClick = { onCategoriaSeleccionada(categoria) },
                label = {
                    Text(
                        nombre.replaceFirstChar {
                            // Convierte la primera letra del nombre de la categoría en mayúscula
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        },
                        color = if (esSeleccionada) BlancoHueso else MarronMedioAcento,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BeigeClaro,
                    selectedContainerColor = MarronMedioAcento,
                    labelColor = MarronOscuro,
                    selectedLabelColor = BlancoHueso
                )
            )
        }

        // Botón de ordenar por precio
        ordenarPorPrecio?.let {
            FilterChip(
                selected = it,
                //Llama a la función onOrdenarClick cuando se selecciona
                onClick = { onOrdenarClick?.invoke() },
                label = {
                    Text(
                        "PRECIO",
                        color = if (it) BlancoHueso else MarronMedioAcento,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BeigeClaro,
                    selectedContainerColor = MarronMedioAcento,
                    labelColor = MarronOscuro,
                    selectedLabelColor = BlancoHueso
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriaChipsPreview() {
    CategoriaChips(
        categorias = listOf(null) + CategoryProducto.entries,
        selectedCategoria = null,
        ordenarPorPrecio = false,
        onCategoriaSeleccionada = {},
        onOrdenarClick = {}
    )
}