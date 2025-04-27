package com.example.readytapas.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarBlancoHueso
import com.example.readytapas.ui.theme.BarMarronMedioAcento
import com.example.readytapas.ui.theme.BarMarronOscuro
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
                        color = if (esSeleccionada) BarBlancoHueso else BarMarronMedioAcento
                    )
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BarBeigeClaro,
                    selectedContainerColor = BarMarronMedioAcento,
                    labelColor = BarMarronOscuro,
                    selectedLabelColor = BarBlancoHueso
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
                        color = if (it) BarBlancoHueso else BarMarronMedioAcento
                    )
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BarBeigeClaro,
                    selectedContainerColor = BarMarronMedioAcento,
                    labelColor = BarMarronOscuro,
                    selectedLabelColor = BarBlancoHueso
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