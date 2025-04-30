package com.example.readytapas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.GrisMedio
import com.example.readytapas.ui.theme.MarronOscuro

/*
* Para usar TopAppBar debemos añadir esta anotación, ya que no es una función
* que esté en su versión final, por lo que pueden surgir errores inesperados
* o incluso que ciertas funcionalidades sean descartadas en futuras versiones.
*/

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
            containerColor = MarronOscuro,
            titleContentColor = BlancoHueso,
        ),
        navigationIcon = {
            // Mostrar el botón de retroceso solo si `showBackButton` es true y onBackClick no es null
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = BlancoHueso
                    )
                }
            }
        },
        title = {
            //Usamos Box para controlar la alineación del texto
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    title,
                    /*
                    * Se evalua el valor de titleAlignment para determinar como se alinea el texto:
                    * TextAlign.Center -> centro.
                    * TextAlign.End -> derecha.
                    * Sino, izquierda.
                    */
                    modifier = Modifier.align(
                        when (titleAlignment) {
                            TextAlign.Center -> Alignment.Center
                            TextAlign.End -> Alignment.CenterEnd
                            else -> Alignment.CenterStart
                        }
                    ),
                    color = BlancoHueso,
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
                    tint = BlancoHueso
                )
            }
            DropdownMenu(
                expanded = expanded,
                containerColor = GrisMedio,
                onDismissRequest = { expanded = false } // Cierra el menú al hacer clic fuera de él
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar sesión", color = BlancoHueso, fontSize = 20.sp) },
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
        onBackClick = {}
    )
}
