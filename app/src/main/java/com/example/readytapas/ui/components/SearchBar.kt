package com.example.readytapas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
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
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MarronOscuro
            )
        },
        placeholder = { Text("Buscar producto ...") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = BeigeClaro,
            focusedContainerColor = BeigeClaro,
            unfocusedBorderColor = MarronOscuro,
            focusedBorderColor = MarronOscuro,
            cursorColor = MarronOscuro,
            focusedTextColor = MarronOscuro,
            unfocusedTextColor = MarronOscuro,
            focusedPlaceholderColor = MarronOscuro.copy(alpha = 0.5f),
            unfocusedPlaceholderColor = MarronOscuro.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var searchText by remember { mutableStateOf("") }

    Surface(color = BeigeClaro) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            searchText = searchText,
            onSearchTextChange = { searchText = it }
        )
    }
}

