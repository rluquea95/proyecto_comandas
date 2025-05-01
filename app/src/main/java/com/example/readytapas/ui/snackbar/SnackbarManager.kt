package com.example.readytapas.ui.snackbar

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SnackbarManager {
    var message = mutableStateOf<Pair<String, Boolean>?>(null)
        private set

    fun showMessage(
        scope: CoroutineScope,
        msg: String,
        isError: Boolean = false,
        durationMillis: Long = 3000
    ) {
        message.value = Pair(msg, isError)
        scope.launch {
            delay(durationMillis)
            message.value = null
        }
    }
}
