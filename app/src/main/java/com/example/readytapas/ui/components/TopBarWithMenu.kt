package com.example.readytapas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readytapas.ui.theme.BarBlancoHuesoTexto
import com.example.readytapas.ui.theme.BarGrisMedio
import com.example.readytapas.ui.theme.BarMarronOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithMenu(
    title: String,
    titleAlignment: TextAlign = TextAlign.Start,
    onLogoutClick: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BarMarronOscuro,
            titleContentColor = BarBlancoHuesoTexto,
        ),
        navigationIcon = {
            // Mostrar el botón de retroceso solo si `showBackButton` es true
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = BarBlancoHuesoTexto
                    )
                }
            }
        },
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    title,
                    modifier = Modifier.align(
                        when (titleAlignment) {
                            TextAlign.Center -> Alignment.Center
                            TextAlign.End -> Alignment.CenterEnd
                            else -> Alignment.CenterStart
                        }
                    ),
                    color = BarBlancoHuesoTexto,
                    fontSize = 24.sp
                )
            }
        },
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
    TopBarWithMenu(
        title = "Ready Tapas",
        onLogoutClick = {},
        showBackButton = true,
        onBackClick = { /* Acción de retroceso */ })
}
