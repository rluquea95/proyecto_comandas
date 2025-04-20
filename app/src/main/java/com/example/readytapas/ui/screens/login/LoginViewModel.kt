package com.example.readytapas.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        //Almacena el email introducido por el usuario
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        //Almacena la contraseña introducida por el usuario
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            showMessageAndHide("Por favor, introduce tu email y contraseña.", isError = true)
            return
        }
        //Lanza una corutina, permitiendo ejecutar código de forma asíncona sin bloquear el hilo principal
        viewModelScope.launch {
            //Modifica la UI para indicar que la operación de login está en proceso
            _uiState.value = _uiState.value.copy(isLoading = true)

            //Se llama a la función login con los datos del usuario y se almacena el resultado de la operación
            val result = authRepository.login(_uiState.value.email, _uiState.value.password)

            //Se elimina el indicador de carga
            _uiState.value = _uiState.value.copy(isLoading = false)

            //Se evalua si se ha podido iniciar sesión o no
            result.onSuccess {
                showMessageAndHide("¡Login exitoso!", isError = false)
                onSuccess()
            }.onFailure { e ->
                showMessageAndHide("Introduce un email y contraseña válidos. ${e.message}", isError = true)
            }
        }
    }

    fun resetPassword() {
        val email = _uiState.value.email.trim()
        if (email.isEmpty()) {
            showMessageAndHide("Por favor, introduce tu email para reestablecer la contraseña.", isError = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.resetPassword(_uiState.value.email)

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                showMessageAndHide("Revisa tu correo para reestablecer la contraseña.", isError = false)
            }.onFailure { e ->
                showMessageAndHide("Error al reestablecer la contraseña. ${e.message}", isError = true)
            }
        }
    }

    private fun showMessageAndHide(message: String, isError: Boolean, durationMillis: Long = 3000) {
        // Actualiza el estado del mensaje dependiendo si es de error o de éxito
        if (isError) {
            _uiState.value = _uiState.value.copy(messageError = message)
        } else {
            _uiState.value = _uiState.value.copy(messageInfo = message)
        }

        // Lanza un retraso y luego limpia el mensaje después de unos segundos
        viewModelScope.launch {
            delay(durationMillis)
            if (isError) {
                _uiState.value = _uiState.value.copy(messageError = null)
            } else {
                _uiState.value = _uiState.value.copy(messageInfo = null)
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val messageError: String? = null,
    val messageInfo: String? = null,
    val resetPasswordEmailSent: Boolean = false
)