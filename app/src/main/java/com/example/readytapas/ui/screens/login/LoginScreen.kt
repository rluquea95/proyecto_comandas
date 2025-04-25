package com.example.readytapas.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.readytapas.R
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarBlancoHuesoTexto
import com.example.readytapas.ui.theme.BarGrisMedio
import com.example.readytapas.ui.theme.BarMarronMedioAcento
import com.example.readytapas.ui.theme.BarMarronOscuro
import com.example.readytapas.ui.theme.Purple40


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    //Almacena el estado actual de LoginViewModel y se actualiza con cada cambio
    val state by viewModel.uiState.collectAsState()

    LoginScreenContent(
        email = state.email,
        password = state.password,
        isLoading = state.isLoading,
        messageError = state.messageError,
        messageInfo = state.messageInfo,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.login(onLoginSuccess) },
        onResetPasswordClick = { viewModel.resetPassword() }
    )
}

//Esta función hace de intermediaria entre LoginScreen y LoginViewModel
@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    isLoading: Boolean = false,
    messageError: String? = null,
    messageInfo: String? = null,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {},
) {
    //Almacena si el usuario pulsa el botón de mostrar contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fodo y capa de color translúcido encima
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BarBeigeClaro.copy(alpha = 0.4f))
        )

        // Ordena todos los componentes de la pantalla centrados horizontalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Box para el título
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BarBeigeClaro.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ready Tapas",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = BarMarronOscuro,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Box que contiene el login
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BarBeigeClaro.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar sesión",
                        modifier = Modifier.padding(top = 16.dp),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = BarMarronOscuro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email", color = BarGrisMedio) },
                        placeholder = {
                            Text(
                                "Introduce tu correo electrónico",
                                color = BarGrisMedio
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Icono Email",
                                tint = BarGrisMedio
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Contraseña", color = BarGrisMedio) },
                        placeholder = { Text("Introduce tu contraseña", color = BarGrisMedio) },
                        singleLine = true,
                        //Si el usuario pulsa el botón de mostrar contraseña, muestra la contraseña
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = BarGrisMedio
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = onResetPasswordClick) {
                        Text(
                            "¿HAS OLVIDADO TU CONTRASEÑA?",
                            color = BarMarronOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onLoginClick,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BarMarronMedioAcento,
                            contentColor = BarBlancoHuesoTexto,
                        )
                    ) {
                        Text("Entrar")
                    }

                    //Formato para los mensajes de error
                    if (messageError != null){
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = messageError,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    //Formato para los mensajes informativos
                    if (messageInfo != null){
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = messageInfo,
                            color = Purple40,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(
        email = "demo@correo.com",
        password = "1234",
        isLoading = false,
        messageInfo = null,
        messageError = null,
    )
}
