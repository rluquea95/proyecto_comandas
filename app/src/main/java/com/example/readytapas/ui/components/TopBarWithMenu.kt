package com.example.readytapas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readytapas.ui.theme.BarBlancoHuesoTexto
import com.example.readytapas.ui.theme.BarGrisMedio
import com.example.readytapas.ui.theme.BarMarronOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithMenu(
    onLogoutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BarMarronOscuro,
            titleContentColor = BarBlancoHuesoTexto,
        ),
        title = { Text("ReadyTapas", color = BarBlancoHuesoTexto, fontSize = 24.sp) },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(34.dp),
                    tint = BarBlancoHuesoTexto
                )
            }
            DropdownMenu(
                expanded = expanded,
                containerColor = BarGrisMedio,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar sesión", color = BarBlancoHuesoTexto, fontSize = 20.sp) },
                    onClick = {
                        expanded = false
                        onLogoutClick()
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBarWithMenu(onLogoutClick = {})
}
