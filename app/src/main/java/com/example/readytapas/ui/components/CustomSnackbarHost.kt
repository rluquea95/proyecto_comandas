package com.example.readytapas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.readytapas.ui.theme.MarronOscuro


@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    snackbarType: SnackbarType
) {
    SnackbarHost(hostState = snackbarHostState) { data ->

        val icon: ImageVector
        val bgColor: Color
        val iconTint: Color
        val textColor: Color

        when (snackbarType) {
            SnackbarType.ERROR -> {
                icon = Icons.Default.Warning
                bgColor = MarronOscuro
                iconTint = Color.White
                textColor = Color.White
            }
            SnackbarType.SUCCESS -> {
                icon = Icons.Default.CheckCircle
                bgColor = Color.Green
                iconTint = Color.Black
                textColor = Color.Black
            }
            SnackbarType.INFO -> {
                icon = Icons.Default.Info
                bgColor = MaterialTheme.colorScheme.primary
                iconTint = Color.White
                textColor = Color.White
            }
        }

        Snackbar(
            containerColor = bgColor,
            contentColor = textColor,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = data.visuals.message,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /* opcional: no tiene dismiss nativo */ }) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = iconTint)
                }
            }
        }
    }
}

enum class SnackbarType {
    INFO,
    SUCCESS,
    ERROR
}
