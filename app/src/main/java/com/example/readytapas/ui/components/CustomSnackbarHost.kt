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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment


@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState, isError: Boolean) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        val icon = if (isError) Icons.Default.Warning else Icons.Default.CheckCircle
        val bgColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
        val iconTint = if (isError) Color.White else Color.Black
        val textColor = if (isError) Color.White else Color.Black

        Snackbar(
            containerColor = bgColor,
            contentColor = Color.White,
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
