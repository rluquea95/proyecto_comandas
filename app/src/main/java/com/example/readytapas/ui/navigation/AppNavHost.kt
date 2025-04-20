package com.example.readytapas.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.readytapas.ui.screens.login.LoginScreen
import com.example.readytapas.ui.screens.mainmenu.MainMenuScreen
import androidx.compose.ui.Alignment
import com.example.readytapas.ui.theme.BarMarronMedioAcento

@Composable
fun AppNavHost(navController: NavHostController, isLoggedIn: Boolean, onLogoutClick: () -> Unit) {
    NavHost(
        navController = navController,
        /*Se almacena la pantalla que se verá cuando la aplicación se inicia
          en caso de que el usuario tenga la sesión iniciada, cargará directamente
          la pantalla mainmenu*/
        startDestination = if (isLoggedIn) "mainmenu" else "login"
    ) {
        composable("login") {
            //Cuando el usuario inicia sesión correctamente, se cambia a la pantalla mainmenu
            LoginScreen(onLoginSuccess = {
                navController.navigate("mainmenu") {
                    /*Elimina la pantalla de login de la pila de navegación para que
                    el usuario no pueda volver pulsando retroceso*/
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("mainmenu") {
            MainMenuScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        // Pantallas vacías para cada destino
        composable("tomarPedido") {
            // Componente vacío para la pantalla "Tomar Pedido"
            Box(modifier = Modifier
                .fillMaxSize()
                .background(BarMarronMedioAcento)
            ) {
                Text("En Preparación", modifier = Modifier.align(Alignment.Center))
            }
        }
        composable("enPreparacion") {
            // Componente vacío para la pantalla "En preparación"
            Box(modifier = Modifier
                .fillMaxSize()
                .background(BarMarronMedioAcento)
            ) {
                Text("En Preparación", modifier = Modifier.align(Alignment.Center))
            }
        }
        composable("platosListos") {
            // Componente vacío para la pantalla "Platos Listos"
            Box(modifier = Modifier
                .fillMaxSize()
                .background(BarMarronMedioAcento)
            ) {
                Text("Platos Listos", modifier = Modifier.align(Alignment.Center))
            }
        }
        composable("pendienteCobro") {
            // Componente vacío para la pantalla "Pendiente de cobro"
            Box(modifier = Modifier
                .fillMaxSize()
                .background(BarMarronMedioAcento)
            ) {
                Text("Pendiente de cobro", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
