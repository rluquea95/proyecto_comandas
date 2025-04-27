package com.example.readytapas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarMarronOscuro

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    placeholder: String = "Buscar..."
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = BarMarronOscuro
            )
        },
        placeholder = { Text(placeholder) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = BarBeigeClaro,
            focusedContainerColor = BarBeigeClaro,
            unfocusedBorderColor = BarMarronOscuro,
            focusedBorderColor = BarMarronOscuro,
            cursorColor = BarMarronOscuro,
            focusedTextColor = BarMarronOscuro,
            unfocusedTextColor = BarMarronOscuro,
            focusedPlaceholderColor = BarMarronOscuro.copy(alpha = 0.5f),
            unfocusedPlaceholderColor = BarMarronOscuro.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchText by remember { mutableStateOf("") }

    Surface(color = BarBeigeClaro) { // Fondo igual que tu app
        SearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Buscar producto..."
        )
    }
}

