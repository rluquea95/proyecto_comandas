package com.example.readytapas.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readytapas.R
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarMarronOscuro

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    //Almacena el estado actual de LoginViewModel y se actualiza con cada cambio
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de la imagen
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = "Fondo de la pantalla login",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa oscura encima del fondo para mejorar legibilidad (opcional)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BarBeigeClaro) // Negro con transparencia (~53%)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                //Muestra el valor del email almacenado
                value = state.email,
                //Se llama a la funcion onEmailChange para actualizar el estado del email en viewModel
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                //Parámetro que oculta de forma visual la contraseña, cambiandola por *
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                //Al pulsar en el botón se intenta iniciar sesión con las credenciales del usuario
                onClick = { viewModel.login(onLoginSuccess) },
                //Si el login está en proceso, el botón se deshabilita
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            //Si el login está en proceso se muestra una barra de progreso
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }

            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

//Variante de LoginScreen para poder cargar la Preview ya que necesita recibir parámetros
@Composable
fun LoginScreenPreviewable(
    email: String,
    password: String,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BarBeigeClaro.copy(alpha = 0.6f))
                )
                Text(
                    text = "Ready Tapas",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = BarMarronOscuro
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Iniciar sesión",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = BarMarronOscuro
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLoginClick,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entrar")
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreenPreviewable(
        email = "demo@correo.com",
        password = "1234",
        isLoading = false,
        errorMessage = null
    )
}
