package com.example.readytapas.ui.snackbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GlobalSnackbarHost(snackbarHostState: SnackbarHostState) {
    val message by SnackbarManager.message

    LaunchedEffect(message) {
        message?.let { (message,_) ->
            snackbarHostState.showSnackbar(message)
        }
    }

    SnackbarHost(hostState = snackbarHostState) { data: SnackbarData ->
        val isError = message?.second ?: false
        Snackbar(
            snackbarData = data,
            containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    }
}
