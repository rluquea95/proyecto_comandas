package com.example.readytapas.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
        _uiState.value = _uiState.value.copy(email = email, emailError = false)
    }

    fun onPasswordChange(password: String) {
        //Almacena la contraseña introducida por el usuario
        _uiState.value = _uiState.value.copy(password = password, passwordError = false)
    }

    fun login(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                emailError = email.isEmpty(),
                passwordError = password.isEmpty(),
                message = "Debes introducir un correo y contraseña",
                isError = true,
            )
            return
        }
        //Lanza una corutina, permitiendo ejecutar código de forma asíncona sin bloquear el hilo principal
        viewModelScope.launch {
            //Modifica la UI para indicar que la operación de login está en proceso
            _uiState.value = _uiState.value.copy(isLoading = true)

            //Se llama a la función login con los datos del usuario y se almacena el resultado de la operación
            val result = authRepository.login(email, password)

            //Se elimina el indicador de carga
            _uiState.value = _uiState.value.copy(isLoading = false)

            //Se evalua si se ha podido iniciar sesión o no
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    message = "Email o contraseña inválidos",
                    isError = true
                )
                Log.e("Login", "Error al iniciar sesión:", e)
            }
        }
    }

    fun resetPassword() {
        val email = _uiState.value.email.trim()
        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                emailError = true,
                message = "Introduce tu email para restablecer la contraseña.",
                isError = true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.resetPassword(email)

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    message = "Revisa tu correo para restablecer la contraseña.",
                    isError = false,
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    message = "Error al restablecer la contraseña.",
                    isError = true,
                )
                Log.e("Login", "Error al reestablecer la contraseña:", e)
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)