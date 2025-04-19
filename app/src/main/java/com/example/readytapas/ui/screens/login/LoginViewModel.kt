package com.example.readytapas.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readytapas.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

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
        //Lanza una corutina, permitiendo ejecutar código de forma asíncona sin bloquear el hilo principal
        viewModelScope.launch {
            //Modifica la UI para indicar que la operación de login está en proceso
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            //Se llama a la función login con los datos del usuario y se almacena el resultado de la operación
            val result = authRepository.login(_uiState.value.email, _uiState.value.password)
            //Se elimina el indicador de carga
            _uiState.value = _uiState.value.copy(isLoading = false)
            //Se evalua si se ha podido iniciar sesión o no
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Error desconocido")
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)